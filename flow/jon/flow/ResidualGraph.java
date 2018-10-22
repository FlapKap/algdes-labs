package flow;


import util.jon.Pair;
import util.jon.Utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util.jon.Utils.*;

public class ResidualGraph {

    class Path {
        public final List<Integer> nodes;
        public final int bottleneck;

        public Path(List<Integer> nodes, int bottleneck) {
            this.nodes = nodes;
            this.bottleneck = bottleneck;
        }
    }

    private int size;
    public int[][] graph;

    public final int source = 0;
    public final int sink;

    public ResidualGraph(int size) {
        this.size = size;
        graph = new int[size][size];
        sink = size - 1;
    }

    public ResidualGraph(Network network) {
        this.size = network.size;
        this.sink = network.sink;
        graph = new int[size][size];
        for (int i = source; i < size; i++) {
            for (int j = source; j < size; j++) {
                graph[i][j] = network.getCapacity(i, j);
            }
        }
    }

    public void addEdge(int from, int to, int capacity) {
        graph[from][to] = capacity;
    }

    private Optional<Path> findPath(Path acc, int currentNode) {
        searchedNodes++;
        if (currentNode == sink) {
            var finalPath = new Path(append(acc.nodes, currentNode), acc.bottleneck);
            return Optional.of(finalPath);
        }
        final var adj = IntStream.range(0, size)
                .boxed()
                .map(adjIndex -> Pair.of(adjIndex, graph[currentNode][adjIndex]))
                .filter(p -> p.right != 0 && !acc.nodes.contains(p.left))
                .collect(Collectors.toList());
        for (var indexAndWeight : adj) {
            final int nextNode = indexAndWeight.left;
            if (acc.nodes.contains(nextNode)) {
                continue;
            }
            final int capacity = indexAndWeight.right;
            final var currentMin = Math.min(capacity, acc.bottleneck);
            final var next = new Path(append(acc.nodes, currentNode), currentMin);
            final var pathOpt = findPath(next, nextNode);
            if (!pathOpt.isPresent()) {
                continue;
            }
            final var path = pathOpt.get();
            if (!path.nodes.contains(sink)) {
                continue;
            }
            return pathOpt;
        }
        return Optional.empty();
    }

    private int searchedNodes;

    /**
     * Returns a new, unique path through the Residual Graph.
     *
     * @return A pair consisting of the vertices on the path and the capacity of the path.
     */
    public Optional<Path> findNewPath() {
        searchedNodes = 0;
        var path = findPath(new Path(List.of(), Integer.MAX_VALUE), source);
        System.out.printf("Searched %d nodes!\n", searchedNodes);
        return path;
    }

    /**
     * Walks through the path, reducing edges by bottleneck capacity
     * and adding backwards edges with bottleneck capacity.
     *
     * @param path the path used to augment the graph.
     */
    public void augmentByPath(Path path) {
        consecutive(path.nodes.stream()).forEach(p -> {
            final int v = p.left;
            final int u = p.right;
            graph[v][u] -= path.bottleneck;
            graph[u][v] += path.bottleneck;
        });
    }

    private Set<Integer> nodeComponent(Set<Integer> acc, int i) {
        if (acc.contains(i)) {
            return acc;
        }
        return IntStream.range(0, size)
                .boxed()
                .map(j -> Pair.of(j, graph[i][j]))
                .filter(p -> p.right != 0)
                .map(p -> p.left)
                .reduce(acc, (s, j) -> concat(s, nodeComponent(append(s, i), j)), Utils::concat);
    }

    /**
     * Returns all nodes currently connected to the source.
     *
     * @return A set of all nodes connected to the source.
     */
    public Set<Integer> sourceComponent() {
        return nodeComponent(new LinkedHashSet<>(), source);
    }
}
