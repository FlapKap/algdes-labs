package jon.chapter5;

import java.util.Objects;

/**
 * The class for holding points.
 * Yep.
 */
public class Point {
    public final String name;

    public final double x;
    public final double y;

    public Point(String label, double x, double y) {
        this.name = label;
        this.x = x;
        this.y = y;
    }

    /**
     * Get the euclidean distance between two points.
     *
     * @param a A point.
     * @param b Another point.
     * @return the euclidean distance between them.
     */
    public static double distance(Point a, Point b) {
        double deltaX = Math.abs(a.x - b.x);
        double deltaY = Math.abs(a.y - b.y);
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0 &&
                Objects.equals(name, point.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, x, y);
        result = 31 * result;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s: (%f, %f)", name, x, y);
    }
}
