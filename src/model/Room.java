import java.awt.Color;

public class Room {
    private double width;
    private double length;
    private double height;
    private String shape; // "Rectangle", "L-Shaped", etc.
    private Color wallColor;
    private Color floorColor;

    public Room(String shape, double width, double length, double height, Color wallColor, Color floorColor) {
        this.shape = shape;
        this.width = width;
        this.length = length;
        this.height = height;
        this.wallColor = wallColor;
        this.floorColor = floorColor;
    }

    // Getters and setters
    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Color getWallColor() {
        return wallColor;
    }

    public void setWallColor(Color wallColor) {
        this.wallColor = wallColor;
    }

    public Color getFloorColor() {
        return floorColor;
    }

    public void setFloorColor(Color floorColor) {
        this.floorColor = floorColor;
    }

    // Helper methods
    public double getVolume() {
        return width * length * height;
    }

    public double getFloorArea() {
        return width * length;
    }
}