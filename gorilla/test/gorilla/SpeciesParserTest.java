package gorilla;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class SpeciesParserTest {

    @Test
    public void parseSpeciesTest() throws FileNotFoundException {
        var testInput = new FileInputStream("./gorilla/data/Toy_FASTAs-in.txt");

        var result = SpeciesParser.parseSpecies(testInput);

        assertTrue(result.stream().anyMatch(s->s.name.equals("Sphinx")), "Result contains 'Sphinx'");
        assertTrue(result.stream().anyMatch(s->s.name.equals("Bandersnatch")), "Result contains 'Bandersnatch'");
        assertTrue(result.stream().anyMatch(s->s.name.equals("Snark")), "'Result contains 'Snark'");
    }
}
