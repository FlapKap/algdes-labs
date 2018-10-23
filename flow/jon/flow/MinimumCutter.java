package flow;

import util.jon.Pair;
import util.jon.Triplet;
import util.jon.UTriplet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MinimumCutter {

    private Network network;
    private ResidualGraph residualGraph;

    @SuppressWarnings("unchecked")
    public MinimumCutter(Network network) {
        this.network = network;
        residualGraph = new ResidualGraph(network);
    }

    private int maxFlow(int currentFlow) {
        var path = residualGraph.findNewPath();
        if (path.nodes.isEmpty() || !path.nodes.contains(residualGraph.sink)) {
            return currentFlow;
        }
        var bottleneck = path.bottleneck;
        residualGraph.augmentByPath(path);
        return maxFlow(currentFlow + bottleneck);
    }

    /**
     * Computes the maximum flow and returns it along with the minimum cut.
     *
     * @return A pair containing the max-flow and the min-cut.
     */
    public Pair<Integer, List<UTriplet<Integer>>> minimumCut() {
        var maxFlow = maxFlow(0); //I guess we don't need this :(
        var sourceComponent = residualGraph.sourceComponent();
//        var maxFlowDeduced = IntStream.range(0, network.size)
//                .filter(i -> !sourceComponent.contains(i))
//                .boxed()
//                .flatMap(i ->
//                        sourceComponent
//                                .stream()
//                                .filter(j -> network.getCapacity(j, i) != 0)
//                                .map(j -> network.getCapacity(j, i))
//                ).reduce(Integer::sum);
        var minimumCut = IntStream.range(0, network.size)
                .filter(i -> !sourceComponent.contains(i))
                .boxed()
                .flatMap(i ->
                        sourceComponent
                                .stream()
                                .filter(j -> network.getCapacity(j, i) != 0)
                                .map(j -> UTriplet.from(j, i, network.getCapacity(j, i)))
                ).collect(Collectors.toList());
        return Pair.of(maxFlow, minimumCut);
    }
}
