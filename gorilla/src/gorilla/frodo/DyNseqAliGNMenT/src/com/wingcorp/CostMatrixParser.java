package com.wingcorp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CostMatrixParser {
    public static CostMatrix ParseCostMatrix(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        CostMatrix matrix = GetMatrix(scanner);
        scanner.close();

        return matrix;
    }

    private static CostMatrix GetMatrix(Scanner scanner) {
        List<Character> charIndex = new ArrayList<>();
        int[][] matrix = new int[0][0];
        var dataCount = 0;

        Pattern headerPattern = Pattern.compile("[A-Z*\\s]+");
        Pattern dataPattern = Pattern.compile("[A-Z*](\\s+[-]?[0-9]+)+");

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            // Do nothing if the line is a comment or is empty
            if (line.startsWith("#") || line.isEmpty()) continue;

            // Check to see if this is a data row
            Matcher dataMatcher = dataPattern.matcher(line);
            if (dataMatcher.matches()) {
                var data = line.split("\\s+");

                for(int i = 1; i < data.length; i++){
                    matrix[dataCount][i-1] = Integer.parseInt(data[i]);
                }

                dataCount++;
            }

            // Check to see if this is a header
            Matcher headerMatcher = headerPattern.matcher(line);
            if (headerMatcher.matches()) {
                // We use this to determine the matrix size
                var header = line.split("\\s+");
                var length = header.length;
                matrix = new int[length + 1][length + 1];
                charIndex = new ArrayList();

                for(int i = 0; i < length; i++) {
                    var str = header[i];
                    charIndex.add(str.charAt(0));
                }
            }
        }

        return new CostMatrix(matrix, charIndex);
    }
}

class CostMatrix {
    int[][] matrix;
    List<Character> charIndex;

    public CostMatrix(int[][] matrix, List<Character> charIndex){
        this.matrix = matrix;
        this.charIndex = charIndex;
    }

    public int Cost(char c1, char c2) {
        var i1 = charIndex.indexOf(c1);
        var i2 = charIndex.indexOf(c2);

        return matrix[i1][i2];
    }
}
