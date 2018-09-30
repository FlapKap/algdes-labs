package cp.jon;

import util.jon.InputReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class for parsing the points files.
 */
public class PointParser {
    public static List<Point> parsePoints(InputStream inputStream) {
        final var numberPattern = "[-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?";
        final var pointPattern = Pattern.compile(String.format("\\W*([^ \t\n]+)[ \t]+(%s)[ \t]+(%s)\\W*", numberPattern, numberPattern));
        final var outList = new LinkedList<Point>();
        try (InputReader inputReader = new InputReader(inputStream)) {
            while (inputReader.ready()) {
                final var match = inputReader.findNext(pointPattern);
                if (match.isEmpty()) {
                    break;
                }
                final var label = match.get(1).trim();
                final var x = Double.parseDouble(match.get(2));
                final var y = Double.parseDouble(match.get(3));
                outList.addFirst(new Point(label, x, y));
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return outList;
    }
}
