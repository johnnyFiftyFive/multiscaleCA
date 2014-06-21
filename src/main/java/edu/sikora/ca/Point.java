package edu.sikora.ca;

/**
 * @author Kamil Sikora
 *         Data: 20.04.14
 */
public class Point {
    public int y;
    public int x;

    public Point(int x, int y) {
        this.y = y;
        this.x = x;
    }

    public Point() {
    }

    public void setCoords(final int pmX, final int pmY) {
        this.y = pmY;
        this.x = pmX;
    }

    @Override
    public String toString() {
        return "x=" + y + " y=" + x;
    }
}
