import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Utility class for rendering room elements in 3D
 */
public class RoomRenderingUtil {

    /**
     * Draws floor texture on a room (wood grain or tiles)
     */
    public static void drawFloorTexture(Graphics2D g2d, Point[] floorPoints, String roomShape) {
        // Skip texture if not rectangular or if points are invalid
        if (floorPoints.length < 4) return;

        // Save original color
        Color originalColor = g2d.getColor();

        // Use a very transparent black for texture lines
        g2d.setColor(new Color(0, 0, 0, 30));

        // Determine if we need wood grain or tiles
        boolean woodGrain = true; // Could make this a user preference later

        if (woodGrain) {
            // Draw wood grain lines
            int linesCount = 15;
            for (int i = 0; i <= linesCount; i++) {
                double ratio = (double) i / linesCount;
                int x1 = (int) (floorPoints[0].x + (floorPoints[1].x - floorPoints[0].x) * ratio);
                int y1 = (int) (floorPoints[0].y + (floorPoints[1].y - floorPoints[0].y) * ratio);
                int x2 = (int) (floorPoints[3].x + (floorPoints[2].x - floorPoints[3].x) * ratio);
                int y2 = (int) (floorPoints[3].y + (floorPoints[2].y - floorPoints[3].y) * ratio);

                g2d.drawLine(x1, y1, x2, y2);
            }
        } else {
            // Draw tile pattern
            int tilesX = 8;
            int tilesY = 8;

            for (int i = 0; i <= tilesX; i++) {
                double ratioX = (double) i / tilesX;
                int x1 = (int) (floorPoints[0].x + (floorPoints[1].x - floorPoints[0].x) * ratioX);
                int y1 = (int) (floorPoints[0].y + (floorPoints[1].y - floorPoints[0].y) * ratioX);
                int x2 = (int) (floorPoints[3].x + (floorPoints[2].x - floorPoints[3].x) * ratioX);
                int y2 = (int) (floorPoints[3].y + (floorPoints[2].y - floorPoints[3].y) * ratioX);

                g2d.drawLine(x1, y1, x2, y2);
            }

            for (int i = 0; i <= tilesY; i++) {
                double ratioY = (double) i / tilesY;
                int x1 = (int) (floorPoints[0].x + (floorPoints[3].x - floorPoints[0].x) * ratioY);
                int y1 = (int) (floorPoints[0].y + (floorPoints[3].y - floorPoints[0].y) * ratioY);
                int x2 = (int) (floorPoints[1].x + (floorPoints[2].x - floorPoints[1].x) * ratioY);
                int y2 = (int) (floorPoints[1].y + (floorPoints[2].y - floorPoints[1].y) * ratioY);

                g2d.drawLine(x1, y1, x2, y2);
            }
        }

        // Restore the original color
        g2d.setColor(originalColor);
    }

    /**
     * Draws a rectangular room in 3D
     */
    public static void drawRectangularRoom3D(Graphics2D g2d, Room room, double width, double length,
                                             double height, double rotationX, double rotationY,
                                             boolean lightingEnabled) {
        // Calculate 3D points for rectangular room
        double[][] floorPoints = {
                {-width / 2, -length / 2, 0},
                {width / 2, -length / 2, 0},
                {width / 2, length / 2, 0},
                {-width / 2, length / 2, 0}
        };

        double[][] ceilingPoints = {
                {-width / 2, -length / 2, height},
                {width / 2, -length / 2, height},
                {width / 2, length / 2, height},
                {-width / 2, length / 2, height}
        };

        // Project 3D points to 2D based on rotation
        Point[] floor2D = new Point[4];
        Point[] ceiling2D = new Point[4];

        for (int i = 0; i < 4; i++) {
            floor2D[i] = RenderingUtil.projectPoint(floorPoints[i], rotationX, rotationY);
            ceiling2D[i] = RenderingUtil.projectPoint(ceilingPoints[i], rotationX, rotationY);
        }

        // CRITICAL: Get wall and floor colors - ensure we have valid colors
        Color wallColor = room.getWallColor();
        Color floorColor = room.getFloorColor();

        // Print debug info to help diagnose color issues
        System.out.println("Room floor color: " + floorColor);
        System.out.println("Room wall color: " + wallColor);

        if (wallColor == null) {
            wallColor = new Color(150, 150, 150); // Default gray if no color set
            System.out.println("Using default wall color: " + wallColor);
        }
        if (floorColor == null) {
            floorColor = new Color(110, 80, 50); // Default wood brown if no color set
            System.out.println("Using default floor color: " + floorColor);
        }

        // IMPORTANT: Draw walls first to ensure floor is drawn on top
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            // Apply different lighting based on wall orientation for 3D effect
            double lightFactor = 1.0 - (i * 0.15); // Each wall gets progressively darker
            Color lightedWallColor = RenderingUtil.applyLighting(wallColor, lightFactor, lightingEnabled);
            g2d.setColor(lightedWallColor);

            Path2D wallPath = new Path2D.Double();
            wallPath.moveTo(floor2D[i].x, floor2D[i].y);
            wallPath.lineTo(floor2D[next].x, floor2D[next].y);
            wallPath.lineTo(ceiling2D[next].x, ceiling2D[next].y);
            wallPath.lineTo(ceiling2D[i].x, ceiling2D[i].y);
            wallPath.closePath();

            g2d.fill(wallPath);
            g2d.setColor(RenderingUtil.darker(wallColor));
            g2d.draw(wallPath);
        }

        // Draw ceiling
        Color lightedCeilingColor = RenderingUtil.applyLighting(wallColor, 0.7, lightingEnabled);
        g2d.setColor(lightedCeilingColor);
        Path2D ceilingPath = new Path2D.Double();
        ceilingPath.moveTo(ceiling2D[0].x, ceiling2D[0].y);

        for (int i = 1; i < 4; i++) {
            ceilingPath.lineTo(ceiling2D[i].x, ceiling2D[i].y);
        }

        ceilingPath.closePath();
        g2d.fill(ceilingPath);
        g2d.setColor(RenderingUtil.darker(wallColor));
        g2d.draw(ceilingPath);

        // CRITICAL: Draw floor using floor color with proper lighting factor
        // Use a higher lighting factor (1.0) to make floor color more visible
        Color lightedFloorColor = RenderingUtil.applyLighting(floorColor, 1.0, lightingEnabled);
        g2d.setColor(lightedFloorColor);

        Path2D floorPath = new Path2D.Double();
        floorPath.moveTo(floor2D[0].x, floor2D[0].y);

        for (int i = 1; i < 4; i++) {
            floorPath.lineTo(floor2D[i].x, floor2D[i].y);
        }

        floorPath.closePath();
        g2d.fill(floorPath);
        g2d.setColor(RenderingUtil.darker(floorColor));
        g2d.draw(floorPath);

        // Add texture to floor after floor is colored
        drawFloorTexture(g2d, floor2D, room.getShape());
    }

    /**
     * Draws an L-shaped room in 3D
     */
    public static void drawLShapedRoom3D(Graphics2D g2d, Room room, double width, double length,
                                         double height, double rotationX, double rotationY,
                                         boolean lightingEnabled) {
        // For L-shaped room, create two rectangles
        double mainWidth = width * 0.8;
        double mainLength = length * 0.8;
        double extensionWidth = width * 0.4;
        double extensionLength = length * 0.4;

        // Draw main rectangle
        drawPartialRoom(g2d, room, -mainWidth / 4, 0, mainWidth, mainLength, height,
                rotationX, rotationY, lightingEnabled);

        // Draw extension (the smaller part of the L)
        drawPartialRoom(g2d, room, -mainWidth / 2 - extensionWidth / 2, mainLength / 2 - extensionLength / 2,
                extensionWidth, extensionLength, height, rotationX, rotationY, lightingEnabled);
    }

    /**
     * Draws a T-shaped room in 3D
     */
    public static void drawTShapedRoom3D(Graphics2D g2d, Room room, double width, double length,
                                         double height, double rotationX, double rotationY,
                                         boolean lightingEnabled) {
        // For T-shaped room, create two rectangles
        double mainWidth = width * 0.8;
        double mainLength = length * 0.6;
        double topWidth = width * 0.5;
        double topLength = length * 0.4;

        // Draw main rectangle (vertical part of T)
        drawPartialRoom(g2d, room, 0, length / 4, mainWidth, mainLength, height,
                rotationX, rotationY, lightingEnabled);

        // Draw top of T (horizontal part)
        drawPartialRoom(g2d, room, 0, -length / 2 + topLength / 2, topWidth, topLength, height,
                rotationX, rotationY, lightingEnabled);
    }

    /**
     * Draws a partial room section (used for complex room shapes)
     */
    public static void drawPartialRoom(Graphics2D g2d, Room room, double offsetX, double offsetY,
                                       double width, double length, double height,
                                       double rotationX, double rotationY, boolean lightingEnabled) {
        // Calculate 3D points for a partial room
        double[][] floorPoints = {
                {offsetX - width/2, offsetY - length/2, 0},
                {offsetX + width/2, offsetY - length/2, 0},
                {offsetX + width/2, offsetY + length/2, 0},
                {offsetX - width/2, offsetY + length/2, 0}
        };

        double[][] ceilingPoints = {
                {offsetX - width/2, offsetY - length/2, height},
                {offsetX + width/2, offsetY - length/2, height},
                {offsetX + width/2, offsetY + length/2, height},
                {offsetX - width/2, offsetY + length/2, height}
        };

        // Project 3D points to 2D
        Point[] floor2D = new Point[4];
        Point[] ceiling2D = new Point[4];

        for (int i = 0; i < 4; i++) {
            floor2D[i] = RenderingUtil.projectPoint(floorPoints[i], rotationX, rotationY);
            ceiling2D[i] = RenderingUtil.projectPoint(ceilingPoints[i], rotationX, rotationY);
        }

        // Get wall and floor colors - ensure we have valid colors
        Color wallColor = room.getWallColor();
        Color floorColor = room.getFloorColor();

        if (wallColor == null) {
            wallColor = new Color(150, 150, 150);
        }
        if (floorColor == null) {
            floorColor = new Color(110, 80, 50); // Default wood brown
        }

        // IMPORTANT: Draw walls first
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            // Apply different lighting based on wall orientation
            double lightFactor = 1.0 - (i * 0.15);
            g2d.setColor(RenderingUtil.applyLighting(wallColor, lightFactor, lightingEnabled));

            Path2D wallPath = new Path2D.Double();
            wallPath.moveTo(floor2D[i].x, floor2D[i].y);
            wallPath.lineTo(floor2D[next].x, floor2D[next].y);
            wallPath.lineTo(ceiling2D[next].x, ceiling2D[next].y);
            wallPath.lineTo(ceiling2D[i].x, ceiling2D[i].y);
            wallPath.closePath();

            g2d.fill(wallPath);
            g2d.setColor(RenderingUtil.darker(wallColor));
            g2d.draw(wallPath);
        }

        // Draw ceiling
        g2d.setColor(RenderingUtil.applyLighting(wallColor, 0.7, lightingEnabled));
        Path2D ceilingPath = new Path2D.Double();
        ceilingPath.moveTo(ceiling2D[0].x, ceiling2D[0].y);

        for (int i = 1; i < 4; i++) {
            ceilingPath.lineTo(ceiling2D[i].x, ceiling2D[i].y);
        }

        ceilingPath.closePath();
        g2d.fill(ceilingPath);
        g2d.setColor(RenderingUtil.darker(wallColor));
        g2d.draw(ceilingPath);

        // Draw floor with proper floor color and full lighting
        g2d.setColor(RenderingUtil.applyLighting(floorColor, 1.0, lightingEnabled));
        Path2D floorPath = new Path2D.Double();
        floorPath.moveTo(floor2D[0].x, floor2D[0].y);

        for (int i = 1; i < 4; i++) {
            floorPath.lineTo(floor2D[i].x, floor2D[i].y);
        }

        floorPath.closePath();
        g2d.fill(floorPath);
        g2d.setColor(RenderingUtil.darker(floorColor));
        g2d.draw(floorPath);

        // Add floor texture last
        drawFloorTexture(g2d, floor2D, room.getShape());
    }
}