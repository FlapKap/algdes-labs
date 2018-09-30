package cp.jon;

/**
 * Data class for storing Pairs and their distances.
 */
public class MeasuredPair {
    public final double dist;
    public final Point a;
    public final Point b;

    public MeasuredPair(double dist, Point a, Point b) {
        this.dist = dist;
        this.a = a;
        this.b = b;
    }
}