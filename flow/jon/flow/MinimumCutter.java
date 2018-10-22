package flow;

import util.jon.Pair;
import util.jon.Triplet;

import java.util.ArrayList;
import java.util.List;

public class MinimumCutter {

    private Network network;
    private ResidualGraph residualGraph;

    @SuppressWarnings("unchecked")
    public MinimumCutter(Network network) {
        this.network = network;
        residualGraph = new ResidualGraph(network);
    }

    private int maxFlow(int currentFlow) {
        var pathOpt = residualGraph.findNewPath();
        if (!pathOpt.isPresent()) {
            return currentFlow;
        }
        var path = pathOpt.get();
        var bottleneck = path.bottleneck;
        residualGraph.augmentByPath(path);
        return maxFlow(currentFlow + bottleneck);
    }

    /**
     * Computes the maximum flow and returns it along with the minimum cut.
     *
     * @return A pair containing the max-flow and the min-cut.
     */
    public Pair<Integer, List<Triplet<Integer, Integer, Integer>>> minimumCut() {
        var maxFlow = maxFlow(0); //I guess we don't need this :(
        var sourceComponent = residualGraph.sourceComponent();
        var outList = new ArrayList<Triplet<Integer, Integer, Integer>>();
        for (var from : sourceComponent) {
            for (var to : sourceComponent) {
                if (from.equals(to)) {
                    continue;
                }
                var cap = network.getCapacity(from, to);
                if (cap != 0) {
                    outList.add(Triplet.of(from, to, cap + network.getCapacity(to, from)));
                }
            }
        }
        return Pair.of(maxFlow, outList);
    }
}
