package jon.chapter5;

import java.util.List;

public class DumbFinder implements ClosestPairFinder {
    @Override
    public MeasuredPair findClosestPair(List<Point> points) {
        double minDist = Double.MAX_VALUE;
        Point left = null;
        Point right = null;
        for (var a: points) {
            for (var b: points) {
                if (a.equals(b)){
                    continue;
                }
                final var dist = Point.distance(a, b);
                if (dist < minDist) {
                    left = a;
                    right = b;
                    minDist = dist;
                }
            }
        }
        return new MeasuredPair(minDist, left, right);
    }
}
