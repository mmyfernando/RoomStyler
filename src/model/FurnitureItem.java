import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

public class FurnitureItem {
    private FurnitureType type;
    private Dimension size; // width, height
    private Point position; // x, y position in the room
    private double rotation; // in degrees
    private Color color;

    public FurnitureItem(FurnitureType type, Dimension size, Point position, Color color) {
        this.type = type;
        this.size = size;
        this.position = position;
        this.color = color;
        this.rotation = 0;
    }

    public FurnitureType getType() {
        return type;
    }

    public void setType(FurnitureType type) {
        this.type = type;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}