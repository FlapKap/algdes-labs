package flow.jon;

import util.jon.Pair;
import util.jon.Triplet;
import static util.jon.Utils.*;

import java.util.Set;
import java.util.function.BiConsumer;

public class MinCut {

    private FlowNetwork flowNetwork;
    private ResidualGraph residualGraph;
    private int source = 0;
    private int sink;
    private boolean[] visited;

    @SuppressWarnings("unchecked")
    public MinCut(int size, BiConsumer<FlowNetwork, ResidualGraph> initializer) {
        flowNetwork = new FlowNetwork(size);
        residualGraph = new ResidualGraph(size);
        initializer.accept(flowNetwork, residualGraph);
        visited = new boolean[size];
        sink = size - 1;
    }

    private Pair<Set<Integer>, Integer> findPath(Pair<Set<Integer>, Integer> acc, int i) {
        if (visited[i]) {
            return acc;
        }
        visited[i] = true;
        if (i == sink) {
            return acc.update((Set<Integer> l, Integer r) ->
                Pair.of(append(l, i), r)
            );
        }
        final var adj = flowNetwork.getNode(i);
        for (int j = 0; j < adj.length; j++) {
            final int to = j;
            final var path = findPath(acc.update((l, r) -> Pair.of(append(l, i), flowNetwork.getCapacity(i, to))), j);
            if (!path.left.contains(sink)) {
                for (var node : path.left) {
                    visited[node] = false;
                }
            } else {
                return path;
            }
        }
        return Pair.of(Set.of(), 0);
    }

    public Set<Integer> minimumCut() {
        return Set.of();
    }
}
