package cp.jon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Dumb {
    public static void main(String[] args) throws FileNotFoundException {
        var inputStream = new FileInputStream("./cp-data/att48-tsp.txt");
        var points = PointParser.parsePoints(inputStream);
        var dumbFinder = new DumbFinder();
        var closest = dumbFinder.findClosestPair(points);
        System.out.printf("n = %d, min_dist = %f, min_pair = (%s, %s)", points.size(), closest.dist, closest.a.name, closest.b.name);
    }
}
