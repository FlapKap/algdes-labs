package util.jon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.BiConsumer;

public class MainHelper {
    public static void acceptArgs(String[] args, BiConsumer<String, InputStream> runForFile) {
        switch (args.length) {
            case 0:
                runForFile.accept("stdin", System.in);
                break;
            default:
                for (var arg : args) {
                    try {
                        var inputStream = new FileInputStream(arg);
                        runForFile.accept(arg, inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
                break;
        }
    }
}
