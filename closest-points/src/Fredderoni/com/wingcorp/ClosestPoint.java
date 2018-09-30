package Fredderoni.com.wingcorp;

import java.util.Arrays;
import java.util.Comparator;

public class ClosestPoint {
    public static Point[] DivideAndConquer (Point[] points, int bruteForceCut) {
        Point[] xSorted = SortByX(points);
        Point[] ySorted = SortByY(points);

        return DivideAndConquerRecursive(xSorted, ySorted, bruteForceCut);
    }

    private static Point[] DivideAndConquerRecursive (Point[] xSorted, Point[] ySorted, int bruteForceCut) {
        int count = xSorted.length;
        if (count <= bruteForceCut) {
            return BruteForce(xSorted);
        }

        int mid = xSorted.length / 2;
        Point median = xSorted[mid];

        Point[] l = Arrays.copyOfRange(xSorted, 0, mid);
        Point[] r = Arrays.copyOfRange(xSorted, mid, xSorted.length);

        Point[] lClosest = DivideAndConquerRecursive(l, ySorted, bruteForceCut);
        Point[] rClosest = DivideAndConquerRecursive(r, ySorted, bruteForceCut);

        if (lClosest[0].Distance(lClosest[1]) < rClosest[0].Distance(rClosest[1])) {
            return CheckMedian(lClosest, median, xSorted);
        } else {
            return CheckMedian(rClosest, median, xSorted);
        }
    }

    private static Point[] CheckMedian (Point[] closestPair, Point median, Point[] xSorted) {
        Point[] closestPairMedian = closestPair;
        double dist = closestPair[0].Distance(closestPair[1]);

        double minX = median.x - (dist / 2);
        double maxX = median.x + (dist / 2);

        Point[] middlePointsByX = Arrays.stream(xSorted)
                                        .filter(p -> p.x <= maxX && p.x >= minX)
                                        .toArray(Point[]::new);
        Point[] middlePointsByY = SortByY(middlePointsByX);

        for(int ix = 0; ix < middlePointsByY.length; ix++) {
            Point px = middlePointsByY[ix];
            for(int iy = ix + 1; iy <= ix + 7 && iy < middlePointsByY.length; iy++){
                Point py = middlePointsByY[iy];
                double d = px.Distance(py);
                if (d < dist) {
                    closestPairMedian[0] = px;
                    closestPairMedian[1] = py;
                    dist = d;
                }
            }
        }
        return closestPairMedian;
    }

    public static Point[] BruteForce (Point[] points) {
        int count = points.length;
        if (count < 2) return null;
        else if (count == 2) return new Point[] {points[0], points[1]};

        double distance = 0f;
        Point[] returnedPair = new Point[2];

        for (Point p1 : points) {
            for (Point p2 : points) {
                if (p1.equals(p2)) continue;
                else if (distance == 0f || p1.Distance(p2) < distance) {
                    distance = p1.Distance(p2);
                    returnedPair[0] = p1;
                    returnedPair[1] = p2;
                    continue;
                }
            }
        }
        return returnedPair;
    }

    private static Point[] SortByX (Point[] points) {
        Point[] sortedPoints = points.clone();
        Arrays.sort(sortedPoints, Comparator.comparingDouble(point -> point.x));
        return sortedPoints;
    }

    private static Point[] SortByY (Point[] points) {
        Point[] sortedPoints = points.clone();
        Arrays.sort(sortedPoints, Comparator.comparingDouble(point -> point.y));
        return sortedPoints;
    }
}
