import java.awt.*;
import java.awt.geom.Path2D;

public class RenderingUtil {
    private static double ambientLight = 0.3; // Base lighting level
    private static double directionalLight = 0.7; // Directional light strength

    /**
     * Projects a 3D point onto a 2D plane based on rotation angles
     */
    public static Point projectPoint(double[] point3D, double rotX, double rotY) {
        // Convert degrees to radians
        double radX = Math.toRadians(rotX);
        double radY = Math.toRadians(rotY);

        // Apply rotations
        double x = point3D[0];
        double y = point3D[1] * Math.cos(radX) - point3D[2] * Math.sin(radX);
        double z = point3D[1] * Math.sin(radX) + point3D[2] * Math.cos(radX);
        double x2 = x * Math.cos(radY) + z * Math.sin(radY);
        double z2 = -x * Math.sin(radY) + z * Math.cos(radY);

        // Simple projection (orthographic)
        return new Point((int)x2, (int)y);
    }

    /**
     * Applies lighting effect to a color
     */
    public static Color applyLighting(Color baseColor, double lightingFactor, boolean lightingEnabled) {
        if (!lightingEnabled) {
            return baseColor;
        }

        // Calculate lighting adjusted color
        double factor = ambientLight + (directionalLight * lightingFactor);
        factor = Math.max(0.0, Math.min(1.5, factor)); // Limit the range
        int r = (int)(baseColor.getRed() * factor);
        int g = (int)(baseColor.getGreen() * factor);
        int b = (int)(baseColor.getBlue() * factor);

        // Clamp RGB values to valid range
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return new Color(r, g, b);
    }

    /**
     * Creates a darker version of a color
     */
    public static Color darker(Color c) {
        return new Color(
                Math.max((int)(c.getRed() * 0.7), 0),
                Math.max((int)(c.getGreen() * 0.7), 0),
                Math.max((int)(c.getBlue() * 0.7), 0)
        );
    }

    /**
     * Creates a lighter version of a color
     */
    public static Color lighter(Color c) {
        return new Color(
                Math.min((int)(c.getRed() * 1.3), 255),
                Math.min((int)(c.getGreen() * 1.3), 255),
                Math.min((int)(c.getBlue() * 1.3), 255)
        );
    }

    /**
     * Draws a 3D box with proper lighting
     */
    public static void drawBox3D(Graphics2D g2d, double x, double y, double z,
                                 double width, double length, double height,
                                 Color color, double lightFactor, double rotationX, double rotationY, boolean lightingEnabled) {
        // Create 3D box points
        double[][] boxPoints = {
                // Bottom face
                {x, y, z},
                {x + width, y, z},
                {x + width, y + length, z},
                {x, y + length, z},
                // Top face
                {x, y, z + height},
                {x + width, y, z + height},
                {x + width, y + length, z + height},
                {x, y + length, z + height}
        };

        // Project points to 2D
        Point[] box2D = new Point[8];
        for (int i = 0; i < 8; i++) {
            box2D[i] = projectPoint(boxPoints[i], rotationX, rotationY);
        }

        // Draw the box
        // Bottom face
        g2d.setColor(applyLighting(color, lightFactor * 0.7, lightingEnabled));
        Path2D bottomFace = new Path2D.Double();
        bottomFace.moveTo(box2D[0].x, box2D[0].y);
        for (int i = 1; i < 4; i++) {
            bottomFace.lineTo(box2D[i].x, box2D[i].y);
        }
        bottomFace.closePath();
        g2d.fill(bottomFace);
        g2d.setColor(darker(color));
        g2d.draw(bottomFace);

        // Side faces
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            // Vary lighting by side
            double sideLightFactor = lightFactor * (1.0 - (i * 0.1));
            g2d.setColor(applyLighting(color, sideLightFactor, lightingEnabled));
            Path2D sideFace = new Path2D.Double();
            sideFace.moveTo(box2D[i].x, box2D[i].y);
            sideFace.lineTo(box2D[next].x, box2D[next].y);
            sideFace.lineTo(box2D[next + 4].x, box2D[next + 4].y);
            sideFace.lineTo(box2D[i + 4].x, box2D[i + 4].y);
            sideFace.closePath();
            g2d.fill(sideFace);
            g2d.setColor(darker(color));
            g2d.draw(sideFace);
        }

        // Top face
        g2d.setColor(applyLighting(color, lightFactor * 1.1, lightingEnabled)); // Top gets more light
        Path2D topFace = new Path2D.Double();
        topFace.moveTo(box2D[4].x, box2D[4].y);
        for (int i = 5; i < 8; i++) {
            topFace.lineTo(box2D[i].x, box2D[i].y);
        }
        topFace.closePath();
        g2d.fill(topFace);
        g2d.setColor(darker(color));
        g2d.draw(topFace);
    }

    /**
     * Draws floor texture on a room
     */
    public static void drawFloorTexture(Graphics2D g2d, Point[] floorPoints, String roomShape) {
        // Skip texture if not rectangular or if points are invalid
        if (floorPoints.length < 4) return;

        g2d.setColor(new Color(0, 0, 0, 30)); // Very transparent black

        // Determine if we need wood grain or tiles
        boolean woodGrain = true; // Could make this a user preference later

        if (woodGrain) {
            // Draw wood grain lines
            int linesCount = 15;
            Point start = floorPoints[0];
            Point end = floorPoints[3];

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
    }

    /**
     * Draws a background gradient
     */
    public static void drawBackground(Graphics2D g2d, int centerX, int centerY) {
        // Create a subtle gradient background to enhance the 3D effect
        GradientPaint gradient = new GradientPaint(
                0, -centerY, new Color(220, 225, 230),
                0, centerY, new Color(190, 200, 220)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(-centerX, -centerY, centerX * 2, centerY * 2);
    }

    /**
     * Draws a legend with room information
     */
    public static void drawLegend(Graphics2D g2d, Room room, int centerX, int centerY) {
        g2d.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black
        g2d.fillRoundRect(-centerX + 10, -centerY + 10, 250, 80, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Room: " + room.getWidth() + "m x " + room.getLength() + "m x " + room.getHeight() + "m",
                -centerX + 20, -centerY + 30);
        g2d.drawString("Shape: " + room.getShape(), -centerX + 20, -centerY + 50);
        g2d.drawString("View: Drag to rotate, Zoom button to adjust", -centerX + 20, -centerY + 70);
    }
}