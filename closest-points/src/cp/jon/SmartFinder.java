package cp.jon;

import util.jon.Utils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Takes a LinkedList of Points and finds the two closest points.
 */
public class SmartFinder implements ClosestPairFinder {

    private long comparisons = 0L;

    public long getLatestComparisons() {
        return comparisons;
    }

    private MeasuredPair selectClosestPair(List<Point> points, MeasuredPair prevPair) {
        Point outA = prevPair.a;
        Point outB = prevPair.b;
        double outDist = prevPair.dist;
        for (Point a : points) {
            for (Point b : points) {
                if (b.equals(a)) {
                    continue;
                }
                var dist = Point.distance(a, b);
                if (dist < outDist) {
                    outA = a;
                    outB = b;
                    outDist = dist;
                }
            }
        }
        comparisons += points.size() * (points.size() - 1L);
        return new MeasuredPair(outDist, outA, outB);
    }


    private MeasuredPair findClosestPairRec(MeasuredPair acc, List<Point> pointsByX, List<Point> pointsByY) {
        if (pointsByX.size() <= 3) {
            return selectClosestPair(Utils.concat(pointsByX, List.of(acc.a, acc.b)), acc);
        } else {
            final int splitX = pointsByX.size() / 2;
            final var leftX = pointsByX.subList(0, splitX);
            final var rightX = pointsByX.subList(splitX, pointsByX.size());

            final int splitY = pointsByY.size() / 2;
            final var leftY = pointsByY.subList(0, splitY);
            final var rightY = pointsByY.subList(splitY, pointsByX.size());

            final var minLeft = findClosestPairRec(acc, leftX, leftY);
            final var minRight = findClosestPairRec(acc, rightX, rightY);
            final var minDist = (minLeft.dist < minRight.dist) ? minLeft : minRight;

            final var xStar = pointsByX.get(splitX).x;
            final var delta = minDist.dist;

            final var s = pointsByX.stream().filter(p -> p.x >= xStar - delta && p.x <= xStar + delta);

            final var sY = s.sorted(Comparator.comparingDouble(p -> p.y)).collect(Collectors.toList());

            final var sLength = sY.size();
            comparisons += (long) (sLength * Math.log(sLength)); //Add n * log(n) where n = sLength, for sorting

            var sYMin = minDist;
            for (int i = 0; i < sLength; i++) {
                final int ahead = i + 15;
                final var toCompare = (ahead < sLength) ? sY.subList(i, ahead)
                        : Utils.concat(sY.subList(0, ahead % sLength ), sY.subList(i, sLength));
                final var newMin = selectClosestPair(toCompare, sYMin);
                if (newMin.dist < sYMin.dist) {
                    sYMin = newMin;
                }
            }

            return sYMin;
        }
    }

    public MeasuredPair findClosestPair(List<Point> points) {
        comparisons = 0L;
        var initial = new MeasuredPair(
                Double.POSITIVE_INFINITY,
                new Point("MAX_POINT", Double.MAX_VALUE, Double.MAX_VALUE),
                new Point("MIN_POINT", -Double.MAX_VALUE, -Double.MAX_VALUE)
        );
        var sortedByX = Utils.sorted(points, p -> p.x);
        var sortedByY = Utils.sorted(points, p -> p.y);
        comparisons += points.size() * Math.log(points.size());
        return findClosestPairRec(initial, sortedByX, sortedByY);
    }
}
