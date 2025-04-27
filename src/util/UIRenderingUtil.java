import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Utility class for rendering UI elements and information displays in 3D view
 */
public class UIRenderingUtil {

    /**
     * Draws a gradient background for the 3D view
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
     * Draws an information legend with room details
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

    /**
     * Draws a help panel with keyboard and mouse controls
     */
    public static void drawHelpPanel(Graphics2D g2d, int width, int height) {
        int panelWidth = 200;
        int panelHeight = 150;
        int x = width - panelWidth - 10;
        int y = height - panelHeight - 10;

        // Draw semi-transparent panel background
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x, y, panelWidth, panelHeight, 10, 10);

        // Draw border
        g2d.setColor(new Color(200, 200, 200, 200));
        g2d.drawRoundRect(x, y, panelWidth, panelHeight, 10, 10);

        // Draw title
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Controls", x + 10, y + 20);

        // Draw control instructions
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        int lineY = y + 40;
        int lineHeight = 16;

        g2d.drawString("• Mouse Drag: Rotate view", x + 10, lineY);
        lineY += lineHeight;
        g2d.drawString("• Zoom Button: Adjust zoom", x + 10, lineY);
        lineY += lineHeight;
        g2d.drawString("• Toggle Lighting: Enhance 3D", x + 10, lineY);
        lineY += lineHeight;
        g2d.drawString("• View 2D: Return to 2D view", x + 10, lineY);
        lineY += lineHeight;
        g2d.drawString("• Save: Save current design", x + 10, lineY);
    }

    /**
     * Draws a tooltip for a furniture item
     */
    public static void drawFurnitureTooltip(Graphics2D g2d, FurnitureItem item, Point position) {
        if (item == null || position == null) return;

        String info = item.getType().getDisplayName();
        Dimension size = item.getSize();
        String dimensions = size.width + "cm × " + size.height + "cm";

        // Calculate tooltip size
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = Math.max(fm.stringWidth(info), fm.stringWidth(dimensions));
        int padding = 5;
        int tooltipWidth = textWidth + (padding * 2);
        int tooltipHeight = (fm.getHeight() * 2) + (padding * 2);

        // Draw tooltip background
        g2d.setColor(new Color(50, 50, 50, 200));
        g2d.fillRoundRect(position.x, position.y - tooltipHeight, tooltipWidth, tooltipHeight, 5, 5);

        // Draw tooltip border
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawRoundRect(position.x, position.y - tooltipHeight, tooltipWidth, tooltipHeight, 5, 5);

        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.drawString(info, position.x + padding, position.y - tooltipHeight + fm.getHeight() + padding);
        g2d.drawString(dimensions, position.x + padding, position.y - padding);
    }

    /**
     * Draws a status message at the bottom of the screen
     */
    public static void drawStatusMessage(Graphics2D g2d, String message, int width, int height) {
        if (message == null || message.isEmpty()) return;

        Font originalFont = g2d.getFont();
        g2d.setFont(new Font("Arial", Font.BOLD, 12));

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int textHeight = fm.getHeight();

        int x = (width - textWidth) / 2;
        int y = height - 30;

        // Draw text background
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(x - 10, y - textHeight, textWidth + 20, textHeight + 10, 10, 10);

        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.drawString(message, x, y);

        // Restore original font
        g2d.setFont(originalFont);
    }

    /**
     * Draws a custom cursor for dragging furniture
     */
    public static void drawDragCursor(Graphics2D g2d, Point position) {
        if (position == null) return;

        int size = 15;
        g2d.setColor(new Color(255, 165, 0, 200)); // Semi-transparent orange

        // Draw crosshair
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawLine(position.x - size, position.y, position.x + size, position.y);
        g2d.drawLine(position.x, position.y - size, position.x, position.y + size);

        // Draw circle
        g2d.drawOval(position.x - 5, position.y - 5, 10, 10);
    }

    /**
     * Draws a simple compass to help with orientation
     */
    public static void drawCompass(Graphics2D g2d, int x, int y, double rotationY) {
        int radius = 30;

        // Save the original transform
        AffineTransform originalTransform = g2d.getTransform();

        // Draw the compass circle
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);

        // Rotate to match the current view rotation
        g2d.translate(x, y);
        g2d.rotate(Math.toRadians(rotationY));

        // Draw N/S/E/W markers
        g2d.setColor(Color.RED);
        g2d.drawLine(0, 0, 0, -radius + 5); // North pointer
        g2d.fillPolygon(
                new int[]{0, -5, 5},
                new int[]{-radius + 5, -radius + 15, -radius + 15},
                3
        );

        g2d.setColor(Color.BLACK);
        g2d.drawLine(0, 0, radius - 5, 0); // East pointer
        g2d.drawLine(0, 0, 0, radius - 5); // South pointer
        g2d.drawLine(0, 0, -radius + 5, 0); // West pointer

        // Draw labels
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("N", -4, -radius + 5);
        g2d.drawString("E", radius - 10, 4);
        g2d.drawString("S", -4, radius);
        g2d.drawString("W", -radius + 2, 4);

        // Restore the original transform
        g2d.setTransform(originalTransform);
    }
}