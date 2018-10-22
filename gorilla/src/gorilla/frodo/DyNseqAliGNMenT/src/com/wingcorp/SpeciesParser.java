package com.wingcorp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeciesParser {
    public static List<Species> ParseSpecies(String path) throws FileNotFoundException {
        var scanner = new Scanner(new File(path));
        var species = GetSpecies(scanner);
        scanner.close();

        return species;
    }

    private static List<Species> GetSpecies(Scanner scanner) {
        var namePattern = Pattern.compile("[\\>](.*)");
        var dataPattern = Pattern.compile("[A-Z]+");

        var species = new ArrayList<Species>();
        String name = null;
        String data = "";

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            // Do nothing if the line is a comment or is empty
            if (line.isEmpty()) continue;

            // Check to see if this is a data row
            Matcher dataMatcher = dataPattern.matcher(line);
            if (dataMatcher.matches()) {
                System.out.println("Data found");
                data += line.trim().toUpperCase();
            }

            // Check to see if this is a header
            Matcher nameMatcher = namePattern.matcher(line);
            if (nameMatcher.matches()) {
                // We use this to determine the matrix size
                System.out.println("Name found");

                if (name != null) {
                    species.add(new Species(name, data.toCharArray()));
                    data = "";
                }

                name = line.trim();
            }
        }
        species.add(new Species(name, data.toCharArray()));
        return species;
    }
}

class Species {
    String name;
    char[] characters;

    public Species(String name, char[] characters){
        this.name = name;
        this.characters = characters;
    }
}
