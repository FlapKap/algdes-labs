package flow;

import util.jon.MainHelper;

import java.io.InputStream;

public class Main {

    private static void runForFile(String filename, InputStream inputStream) {
        System.out.println("#####################################################################");
        System.out.printf("Parsing input from %s:\n", filename);
        long startParse = System.currentTimeMillis();
        var labelsAndNetwork = NetworkParser.parseNetwork(inputStream);
        long endParse = System.currentTimeMillis() - startParse;
        var labels = labelsAndNetwork.left;
        var network = labelsAndNetwork.right;
        System.out.printf("Parsed network of size: %d in %dms\n", network.size, endParse);
        long startCompute = System.currentTimeMillis();
        MinimumCutter minimumCutter = new MinimumCutter(network);
        var maxFlowMinCut = minimumCutter.minimumCut();
        System.out.printf("Finished computing in %dms\n", System.currentTimeMillis() - startCompute);
        System.out.printf("Max-Flow determined to be: %d\n", maxFlowMinCut.left);
        System.out.println("Minimum-Cut determined to be:\n");
        maxFlowMinCut.right.forEach(cut ->
                System.out.printf("%s %s %d\n", labels[cut.left], labels[cut.middle], cut.right));
    }

    public static void main(String[] args) {
        MainHelper.acceptArgs(args, Main::runForFile);
    }
}
