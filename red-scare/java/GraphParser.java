package PACKAGE_NAME;

public class GraphParser {
    private static GraphParser ourInstance = new GraphParser();

    public static GraphParser getInstance() {
        return ourInstance;
    }

    private GraphParser() {
    }
}
