package uk.co.kent.coalas.chair.joystick;

/**
 * Created by coalas-kent on 30/03/15.
 */
public class Circle2D {

    /**
     * The x coordinate of the upper left corner of the circle's
     * bounding rectangle.
     */
    public double x;
    /**
     * The y coordinate of the upper left corner of the circle's
     * bounding rectangle.
     */
    public double y;

    /**
     * The radius of the circle.
     */
    public double radius;
    /**
     * The width of the circle's bounding rectangle.
     */
    public double width;

    /**
     * The height of the circle's bounding rectangle.
     */
    public double height;
    /**
     * The X coordinate of the center of the circle.
     */
    public double centerX;
    /**
     * The Y coordinate of the center of the circle.
     */
    public double centerY;


    Circle2D(double centerX, double centerY, double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        x = centerX - radius;
        y = centerY - radius;
        width = radius * 2;
        height = width;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getCenterX() {
        return centerX;
    }

    public boolean isEmpty() {
        return width <= 0.0 || height <= 0.0;
    }

    public void setFrame(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.width =  w;
        this.height = h;
    }

    public boolean contains(double px, double py) {
        if (isEmpty()) {
            return false;
        }
        double a = (px - getX()) / getWidth() - 0.5;
        double b = (py - getY()) / getHeight() - 0.5;
        return a * a + b * b < 0.25;
    }

}
