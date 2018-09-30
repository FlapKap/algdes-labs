package Fredderoni.com.wingcorp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    public static ParsedData ParseFile(String path) {
        try {
            Scanner scanner = new Scanner(new File(path));

            Header header = GetHeader(scanner);
            Point[] nodes = GetPoints(header, scanner);

            scanner.close();

            return new ParsedData(header, nodes);
        } catch (FileNotFoundException e) {
            System.err.println("The specified file could not be found: " + path);
            System.exit(1);
            return null;
        }
    }

    private static Header GetHeader (Scanner scanner) {
        Header header = new Header();

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith("NAME")) {
                // Split it on ":" and trim
                header.Name = line.split(":")[1].trim();
            } else if (line.startsWith("COMMENT")) {
                // Split it on ":" and trim
                header.Comment = line.split(":")[1].trim();
            } else if (line.startsWith("TYPE")) {
                // Split it on ":" and trim
                header.FileType = line.split(":")[1].trim();
            } else if (line.startsWith("DIMENSION")) {
                // Split it on ":" and trim
                header.Dimension = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.startsWith("EDGE_WEIGHT_TYPE")) {
                // Split it on ":" and trim
                header.EdgeWeightType = line.split(":")[1].trim();
            }
            else if (line.matches("NODE_COORD_SECTION")) return header;
            else System.out.println("Found an unhandled piece of Header information: " + line);
        }

        return header;
    }

    private static Point[] GetPoints(Header header, Scanner scanner) {
        Point[] nodes = new Point[header.Dimension];
        Integer i = 0;

        while(i < header.Dimension && scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            // Split it on spaces
            String[] parts = line.split("\\s+");

            // Parse the last two parts as floats
            float x = Float.parseFloat(parts[1].trim());
            float y = Float.parseFloat(parts[2].trim());

            nodes[i++] = new Point(x, y);
        }

        return nodes;
    }

    static class Header {
        public String Name = "Unnamed";
        public String Comment = "";
        public String FileType = "Unknown";
        public Integer Dimension = -1;
        public String EdgeWeightType = "Unknown";
    }

    public static class ParsedData {
        public Header Header;
        public Point[] Points;

        public ParsedData(Header header, Point[] points) {
            this.Header = header;
            this.Points = points;
        }
    }
}
