package flow;

import util.jon.MainHelper;

import java.io.InputStream;

public class Main {

    private static void runForFile(String filename, InputStream inputStream) {
        System.out.printf("Parsing input from %s:\n", filename);
        var labelsAndNetwork = NetworkParser.parseNetwork(inputStream);
        var labels = labelsAndNetwork.left;
        var network = labelsAndNetwork.right;
        MinimumCutter minimumCutter = new MinimumCutter(network);
        var maxFlowMinCut = minimumCutter.minimumCut();
        System.out.printf("Max-Flow determined to be %d", maxFlowMinCut.left);
        System.out.println("Minimum-Cut determined to be:");
        maxFlowMinCut.right.forEach(cut ->
                System.out.printf("%s %s %d\n", labels[cut.left], labels[cut.middle], cut.right));
    }

    public static void main(String[] args) {
        MainHelper.acceptArgs(args, Main::runForFile);
    }
}
