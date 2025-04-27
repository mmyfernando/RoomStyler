import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Utility class for rendering furniture items in 3D
 */
public class FurnitureRenderingUtil {

    /**
     * Draws a generic furniture item in 3D
     */
    public static void drawGenericFurniture3D(Graphics2D g2d, FurnitureItem item,
                                              double scale, double rotationX, double rotationY,
                                              boolean lightingEnabled) {
        Point pos = item.getPosition();
        Dimension size = item.getSize();

        // Convert from 2D design coordinates to our 3D world
        double x = (pos.x - 400) * scale; // Adjust based on your 2D coordinate system
        double y = (pos.y - 300) * scale;

        // Calculate 3D box for furniture
        double width = size.width * scale;
        double length = size.height * scale;
        double height = 30 * scale; // Default height for furniture

        // Adjust position to be relative to room center
        double[][] boxPoints = {
                // Bottom face
                {x - width/2, y - length/2, 0},
                {x + width/2, y - length/2, 0},
                {x + width/2, y + length/2, 0},
                {x - width/2, y + length/2, 0},
                // Top face
                {x - width/2, y - length/2, height},
                {x + width/2, y - length/2, height},
                {x + width/2, y + length/2, height},
                {x - width/2, y + length/2, height}
        };

        // Project points
        Point[] box2D = new Point[8];
        for (int i = 0; i < 8; i++) {
            box2D[i] = RenderingUtil.projectPoint(boxPoints[i], rotationX, rotationY);
        }

        // Get furniture color
        Color itemColor = item.getColor();
        if (itemColor == null) {
            itemColor = Color.orange; // Default color
        }

        // Draw bottom face
        g2d.setColor(RenderingUtil.applyLighting(itemColor, 0.8, lightingEnabled));
        Path2D bottomFace = new Path2D.Double();
        bottomFace.moveTo(box2D[0].x, box2D[0].y);
        for (int i = 1; i < 4; i++) {
            bottomFace.lineTo(box2D[i].x, box2D[i].y);
        }
        bottomFace.closePath();
        g2d.fill(bottomFace);
        g2d.setColor(RenderingUtil.darker(itemColor));
        g2d.draw(bottomFace);

        // Draw sides with varied lighting
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            // Apply different lighting based on side orientation
            double lightFactor = 0.9 - (i * 0.15);
            g2d.setColor(RenderingUtil.applyLighting(itemColor, lightFactor, lightingEnabled));
            Path2D sideFace = new Path2D.Double();
            sideFace.moveTo(box2D[i].x, box2D[i].y);
            sideFace.lineTo(box2D[next].x, box2D[next].y);
            sideFace.lineTo(box2D[next + 4].x, box2D[next + 4].y);
            sideFace.lineTo(box2D[i + 4].x, box2D[i + 4].y);
            sideFace.closePath();
            g2d.fill(sideFace);
            g2d.setColor(RenderingUtil.darker(itemColor));
            g2d.draw(sideFace);
        }

        // Draw top face
        g2d.setColor(RenderingUtil.applyLighting(itemColor, 1.0, lightingEnabled)); // Top gets full light
        Path2D topFace = new Path2D.Double();
        topFace.moveTo(box2D[4].x, box2D[4].y);
        for (int i = 5; i < 8; i++) {
            topFace.lineTo(box2D[i].x, box2D[i].y);
        }
        topFace.closePath();
        g2d.fill(topFace);
        g2d.setColor(RenderingUtil.darker(itemColor));
        g2d.draw(topFace);

        // Draw label
        g2d.setColor(Color.BLACK);
        g2d.drawString(item.getType().getDisplayName(), box2D[4].x, box2D[4].y - 5);
    }

    /**
     * Draws a chair in 3D with detailed features
     */
    public static void drawChair3D(Graphics2D g2d, FurnitureItem item,
                                   double scale, double rotationX, double rotationY,
                                   boolean lightingEnabled) {
        Point pos = item.getPosition();
        Dimension size = item.getSize();
        Color itemColor = item.getColor();

        if (itemColor == null) {
            itemColor = Color.orange;
        }

        // Convert from 2D design coordinates to 3D world
        double x = (pos.x - 400) * scale;
        double y = (pos.y - 300) * scale;

        // Chair dimensions
        double width = size.width * scale;
        double length = size.height * scale;
        double seatHeight = 20 * scale;
        double backHeight = 40 * scale;
        double legThickness = width * 0.1;

        // Draw the chair seat (the main box)
        double[][] seatPoints = {
                // Bottom face
                {x - width/2, y - length/2, 0},
                {x + width/2, y - length/2, 0},
                {x + width/2, y + length/2, 0},
                {x - width/2, y + length/2, 0},
                // Top face
                {x - width/2, y - length/2, seatHeight},
                {x + width/2, y - length/2, seatHeight},
                {x + width/2, y + length/2, seatHeight},
                {x - width/2, y + length/2, seatHeight}
        };

        // Draw the chair back (vertical box behind seat)
        double backWidth = width * 0.8;
        double backThickness = length * 0.15;
        double[][] backPoints = {
                // Bottom points (at seat level)
                {x - backWidth/2, y + length/2 - backThickness, seatHeight},
                {x + backWidth/2, y + length/2 - backThickness, seatHeight},
                {x + backWidth/2, y + length/2, seatHeight},
                {x - backWidth/2, y + length/2, seatHeight},
                // Top points
                {x - backWidth/2, y + length/2 - backThickness, seatHeight + backHeight},
                {x + backWidth/2, y + length/2 - backThickness, seatHeight + backHeight},
                {x + backWidth/2, y + length/2, seatHeight + backHeight},
                {x - backWidth/2, y + length/2, seatHeight + backHeight}
        };

        // Project all the 3D points to 2D
        Point[] seatPoints2D = new Point[8];
        Point[] backPoints2D = new Point[8];

        for (int i = 0; i < 8; i++) {
            seatPoints2D[i] = RenderingUtil.projectPoint(seatPoints[i], rotationX, rotationY);
            backPoints2D[i] = RenderingUtil.projectPoint(backPoints[i], rotationX, rotationY);
        }

        // Draw the chair parts
        // 1. First draw the legs
        Color legColor = RenderingUtil.darker(RenderingUtil.darker(itemColor));

        // Front left leg
        RenderingUtil.drawBox3D(g2d,
                x - width/2 + legThickness/2, y - length/2 + legThickness/2, 0,
                legThickness, legThickness, seatHeight,
                legColor, 0.9, rotationX, rotationY, lightingEnabled);

        // Front right leg
        RenderingUtil.drawBox3D(g2d,
                x + width/2 - legThickness*1.5, y - length/2 + legThickness/2, 0,
                legThickness, legThickness, seatHeight,
                legColor, 0.9, rotationX, rotationY, lightingEnabled);

        // Back left leg
        RenderingUtil.drawBox3D(g2d,
                x - width/2 + legThickness/2, y + length/2 - legThickness*1.5, 0,
                legThickness, legThickness, seatHeight,
                legColor, 0.8, rotationX, rotationY, lightingEnabled);

        // Back right leg
        RenderingUtil.drawBox3D(g2d,
                x + width/2 - legThickness*1.5, y + length/2 - legThickness*1.5, 0,
                legThickness, legThickness, seatHeight,
                legColor, 0.8, rotationX, rotationY, lightingEnabled);

        // 2. Draw the seat
        Color seatColor = itemColor;
        g2d.setColor(RenderingUtil.applyLighting(seatColor, 1.0, lightingEnabled));

        // Top face of seat
        Path2D topFace = new Path2D.Double();
        topFace.moveTo(seatPoints2D[4].x, seatPoints2D[4].y);

        for (int i = 5; i < 8; i++) {
            topFace.lineTo(seatPoints2D[i].x, seatPoints2D[i].y);
        }

        topFace.closePath();
        g2d.fill(topFace);
        g2d.setColor(RenderingUtil.darker(seatColor));
        g2d.draw(topFace);

        // Sides of seat
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            // Apply different lighting based on side orientation
            double lightFactor = 0.9 - (i * 0.1);
            g2d.setColor(RenderingUtil.applyLighting(seatColor, lightFactor, lightingEnabled));

            Path2D sideFace = new Path2D.Double();
            sideFace.moveTo(seatPoints2D[i].x, seatPoints2D[i].y);
            sideFace.lineTo(seatPoints2D[next].x, seatPoints2D[next].y);
            sideFace.lineTo(seatPoints2D[next + 4].x, seatPoints2D[next + 4].y);
            sideFace.lineTo(seatPoints2D[i + 4].x, seatPoints2D[i + 4].y);
            sideFace.closePath();

            g2d.fill(sideFace);
            g2d.setColor(RenderingUtil.darker(seatColor));
            g2d.draw(sideFace);
        }

        // 3. Draw the chair back
        Color backColor = RenderingUtil.darker(itemColor);

        // Front face of back
        g2d.setColor(RenderingUtil.applyLighting(backColor, 0.95, lightingEnabled));
        Path2D backFrontFace = new Path2D.Double();
        backFrontFace.moveTo(backPoints2D[0].x, backPoints2D[0].y);
        backFrontFace.lineTo(backPoints2D[1].x, backPoints2D[1].y);
        backFrontFace.lineTo(backPoints2D[5].x, backPoints2D[5].y);
        backFrontFace.lineTo(backPoints2D[4].x, backPoints2D[4].y);
        backFrontFace.closePath();

        g2d.fill(backFrontFace);
        g2d.setColor(RenderingUtil.darker(backColor));
        g2d.draw(backFrontFace);

        // Top of back
        g2d.setColor(RenderingUtil.applyLighting(backColor, 1.0, lightingEnabled));
        Path2D backTopFace = new Path2D.Double();
        backTopFace.moveTo(backPoints2D[4].x, backPoints2D[4].y);
        backTopFace.lineTo(backPoints2D[5].x, backPoints2D[5].y);
        backTopFace.lineTo(backPoints2D[6].x, backPoints2D[6].y);
        backTopFace.lineTo(backPoints2D[7].x, backPoints2D[7].y);
        backTopFace.closePath();

        g2d.fill(backTopFace);
        g2d.setColor(RenderingUtil.darker(backColor));
        g2d.draw(backTopFace);

        // Sides of back
        g2d.setColor(RenderingUtil.applyLighting(backColor, 0.85, lightingEnabled));
        Path2D backSide1 = new Path2D.Double();
        backSide1.moveTo(backPoints2D[1].x, backPoints2D[1].y);
        backSide1.lineTo(backPoints2D[2].x, backPoints2D[2].y);
        backSide1.lineTo(backPoints2D[6].x, backPoints2D[6].y);
        backSide1.lineTo(backPoints2D[5].x, backPoints2D[5].y);
        backSide1.closePath();

        g2d.fill(backSide1);
        g2d.setColor(RenderingUtil.darker(backColor));
        g2d.draw(backSide1);

        g2d.setColor(RenderingUtil.applyLighting(backColor, 0.8, lightingEnabled));
        Path2D backSide2 = new Path2D.Double();
        backSide2.moveTo(backPoints2D[0].x, backPoints2D[0].y);
        backSide2.lineTo(backPoints2D[3].x, backPoints2D[3].y);
        backSide2.lineTo(backPoints2D[7].x, backPoints2D[7].y);
        backSide2.lineTo(backPoints2D[4].x, backPoints2D[4].y);
        backSide2.closePath();

        g2d.fill(backSide2);
        g2d.setColor(RenderingUtil.darker(backColor));
        g2d.draw(backSide2);

        // Back face of back (usually not visible)
        g2d.setColor(RenderingUtil.applyLighting(backColor, 0.7, lightingEnabled));
        Path2D backRearFace = new Path2D.Double();
        backRearFace.moveTo(backPoints2D[2].x, backPoints2D[2].y);
        backRearFace.lineTo(backPoints2D[3].x, backPoints2D[3].y);
        backRearFace.lineTo(backPoints2D[7].x, backPoints2D[7].y);
        backRearFace.lineTo(backPoints2D[6].x, backPoints2D[6].y);
        backRearFace.closePath();

        g2d.fill(backRearFace);
        g2d.setColor(RenderingUtil.darker(backColor));
        g2d.draw(backRearFace);

        // Add label
        g2d.setColor(Color.BLACK);
        g2d.drawString("Chair", seatPoints2D[4].x, seatPoints2D[4].y - 15);
    }

    /**
     * Draws a dining table in 3D with detailed features
     */
    public static void drawDiningTable3D(Graphics2D g2d, FurnitureItem item,
                                         double scale, double rotationX, double rotationY,
                                         boolean lightingEnabled) {
        Point pos = item.getPosition();
        Dimension size = item.getSize();
        Color itemColor = item.getColor();

        if (itemColor == null) {
            itemColor = Color.orange;
        }

        // Convert from 2D design coordinates to 3D world
        double x = (pos.x - 400) * scale;
        double y = (pos.y - 300) * scale;

        // Table dimensions
        double width = size.width * scale;
        double length = size.height * scale;
        double tableTopHeight = 40 * scale;
        double tableTopThickness = 5 * scale;
        double legThickness = width * 0.06;

        // Draw the table legs first
        Color legColor = RenderingUtil.darker(RenderingUtil.darker(itemColor));

        // Front left leg
        RenderingUtil.drawBox3D(g2d,
                x - width/2 + legThickness/2, y - length/2 + legThickness/2, 0,
                legThickness, legThickness, tableTopHeight,
                legColor, 0.9, rotationX, rotationY, lightingEnabled);

        // Front right leg
        RenderingUtil.drawBox3D(g2d,
                x + width/2 - legThickness*1.5, y - length/2 + legThickness/2, 0,
                legThickness, legThickness, tableTopHeight,
                legColor, 0.9, rotationX, rotationY, lightingEnabled);

        // Back left leg
        RenderingUtil.drawBox3D(g2d,
                x - width/2 + legThickness/2, y + length/2 - legThickness*1.5, 0,
                legThickness, legThickness, tableTopHeight,
                legColor, 0.8, rotationX, rotationY, lightingEnabled);

        // Back right leg
        RenderingUtil.drawBox3D(g2d,
                x + width/2 - legThickness*1.5, y + length/2 - legThickness*1.5, 0,
                legThickness, legThickness, tableTopHeight,
                legColor, 0.8, rotationX, rotationY, lightingEnabled);

        // Draw horizontal support beams between legs for stability
        // Front beam
        RenderingUtil.drawBox3D(g2d,
                x - width/2 + legThickness, y - length/2 + legThickness/2, tableTopHeight/3,
                width - 2*legThickness, legThickness, legThickness,
                legColor, 0.85, rotationX, rotationY, lightingEnabled);

        // Back beam
        RenderingUtil.drawBox3D(g2d,
                x - width/2 + legThickness, y + length/2 - legThickness*1.5, tableTopHeight/3,
                width - 2*legThickness, legThickness, legThickness,
                legColor, 0.8, rotationX, rotationY, lightingEnabled);

        // Left beam
        RenderingUtil.drawBox3D(g2d,
                x - width/2 + legThickness/2, y - length/2 + legThickness*1.5, tableTopHeight/3,
                legThickness, length - 3*legThickness, legThickness,
                legColor, 0.83, rotationX, rotationY, lightingEnabled);

        // Right beam
        RenderingUtil.drawBox3D(g2d,
                x + width/2 - legThickness*1.5, y - length/2 + legThickness*1.5, tableTopHeight/3,
                legThickness, length - 3*legThickness, legThickness,
                legColor, 0.82, rotationX, rotationY, lightingEnabled);

        // Draw the table top (with a slight overhang)
        double overhang = 5 * scale;
        RenderingUtil.drawBox3D(g2d,
                x - width/2 - overhang, y - length/2 - overhang, tableTopHeight,
                width + 2*overhang, length + 2*overhang, tableTopThickness,
                itemColor, 1.0, rotationX, rotationY, lightingEnabled);

        // Add label
        Point tableTopPoint = RenderingUtil.projectPoint(
                new double[] {x, y, tableTopHeight + tableTopThickness},
                rotationX, rotationY);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Dining Table", tableTopPoint.x - 30, tableTopPoint.y - 5);
    }

    /**
     * Draws a side table in 3D with detailed features
     */
    public static void drawSideTable3D(Graphics2D g2d, FurnitureItem item,
                                       double scale, double rotationX, double rotationY,
                                       boolean lightingEnabled) {
        Point pos = item.getPosition();
        Dimension size = item.getSize();
        Color itemColor = item.getColor();

        if (itemColor == null) {
            itemColor = Color.orange;
        }

        // Convert from 2D design coordinates to 3D world
        double x = (pos.x - 400) * scale;
        double y = (pos.y - 300) * scale;

        // Table dimensions
        double width = size.width * scale;
        double length = size.height * scale;
        double tableHeight = 30 * scale;
        double tableTopThickness = 3 * scale;
        double legThickness = Math.min(width, length) * 0.15;

        // For side table, let's make a pedestal style with a central column
        // Draw the central support column
        RenderingUtil.drawBox3D(g2d,
                x - legThickness/2, y - legThickness/2, 0,
                legThickness, legThickness, tableHeight,
                RenderingUtil.darker(itemColor), 0.9, rotationX, rotationY, lightingEnabled);

        // Draw a base
        double baseSize = Math.min(width, length) * 0.6;
        RenderingUtil.drawBox3D(g2d,
                x - baseSize/2, y - baseSize/2, 0,
                baseSize, baseSize, 3 * scale,
                RenderingUtil.darker(RenderingUtil.darker(itemColor)), 0.8, rotationX, rotationY, lightingEnabled);

        // Draw the table top (with a slight overhang)
        RenderingUtil.drawBox3D(g2d,
                x - width/2, y - length/2, tableHeight,
                width, length, tableTopThickness,
                itemColor, 1.0, rotationX, rotationY, lightingEnabled);

        // Add label
        Point tableTopPoint = RenderingUtil.projectPoint(
                new double[] {x, y, tableHeight + tableTopThickness},
                rotationX, rotationY);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Side Table", tableTopPoint.x - 25, tableTopPoint.y - 5);
    }
}