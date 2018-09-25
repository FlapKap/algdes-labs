package jon.chapter5;

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
                "%s: n = %d, min_dist = %f, min_pair = (%s, %s)\n",
                file,
                (points.size()),
                closestPair.dist,
                closestPair.a.name,
                closestPair.b.name
        );
    }

    public static void main(String[] args) {
        final var start = System.currentTimeMillis();
        switch (args.length) {
            case 0:
                runForFile("stdin", System.in);
                break;
            default:
                for (var arg : args) {
                    try {
                        var inputStream = new FileInputStream(arg);
                        runForFile(arg, inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                break;
        }
        System.out.printf("Total elapsed time: %dms\n", System.currentTimeMillis() - start);
    }
}
