package Fredderoni.com.wingcorp;

import Fredderoni.com.wingcorp.Parser.ParsedData;

public class Main {

    public static void main(String[] args) {
        switch (args.length) {
            case 1:
                // Parse the file
                ParsedData parsedData  = Parser.ParseFile(args[0]);

                // Running the divide and conquer algorithm
                Point[] pair = ClosestPoint.DivideAndConquer(parsedData.Points, 3);

                // Print result
                System.out.println(pair[0].Distance(pair[1]));
                break;
            default:
                System.err.println("Invalid arguments supplied. Please specify a file with a valid TSP format");
        }
    }
}
