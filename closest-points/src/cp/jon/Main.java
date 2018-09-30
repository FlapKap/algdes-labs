package cp.jon;

import util.jon.MainHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main {

    private static void runForFile(String file, InputStream inputStream) {
        System.out.printf("Parsing file: %s\n", file);
        final var parseStart = System.currentTimeMillis();
        final var points = PointParser.parsePoints(inputStream);
        final var parseEnd = System.currentTimeMillis();
        System.out.printf("Parsed %d points in %dms\n", points.size(), (parseEnd - parseStart));

        final var searchStart = System.currentTimeMillis();
        final var closestPairFinder = new SmartFinder();
        final var closestPair = closestPairFinder.findClosestPair(points);
        final var searchEnd = System.currentTimeMillis();
        System.out.printf("Found closest pair in %dms\n", (searchEnd - searchStart));

        //Prints out "<file>: n = <number of points>, min_dist = <closest distance>, min_pair = <closest pair>"
        System.out.printf(
                "%s: n = %d, min_dist = %f, min_pair = [(%f, %f), (%f, %f)]\n",
                file,
                (points.size()),
                closestPair.dist,
                closestPair.a.x,
                closestPair.a.y,
                closestPair.b.x,
                closestPair.b.y
        );
    }

    public static void main(String[] args) {
        final var start = System.currentTimeMillis();
        MainHelper.acceptArgs(args, Main::runForFile);
        System.out.printf("Total elapsed time: %dms\n", System.currentTimeMillis() - start);
    }
}
