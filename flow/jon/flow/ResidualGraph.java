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

    private Path findPath(Path acc, int currentNode) {
        searchedNodes++;
        if (currentNode == sink) {
            return new Path(append(acc.nodes, currentNode), acc.bottleneck);
        }
        final var adj = IntStream.range(0, size)
                .boxed()
                .map(adjIndex -> Pair.of(adjIndex, graph[currentNode][adjIndex]))
                .filter(p -> p.right != 0 && !acc.nodes.contains(p.left))
//                .sorted(Comparator.comparingInt(p -> -p.right))
                .collect(Collectors.toList());
        for (var indexAndWeight : adj) {
            final int nextNode = indexAndWeight.left;
            if (acc.nodes.contains(nextNode)) {
                continue;
            }
            final int capacity = indexAndWeight.right;
            final var currentMin = Math.min(capacity, acc.bottleneck);
            final var next = new Path(append(acc.nodes, currentNode), currentMin);
            final var path = findPath(next, nextNode);
            if (!path.nodes.contains(sink)) {
                continue;
            }
            return path;
        }
        return acc;
    }

    private int searchedNodes;

    /**
     * Returns a new, unique path through the Residual Graph.
     *
     * @return A pair consisting of the vertices on the path and the capacity of the path.
     */
    public Path findNewPath() {
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
        for (int i = 0; i < path.nodes.size() - 1; i++) {
            final int v = path.nodes.get(i);
            final int u = path.nodes.get(i + 1);
            graph[v][u] -= path.bottleneck;
            graph[u][v] += path.bottleneck;
        }
    }

    /**
     * Returns all nodes currently connected to the source.
     *
     * @return A set of all nodes connected to the source.
     */
    public Set<Integer> sourceComponent() {
        var visited = new boolean[size];
        var queue = new LinkedList<Integer>();
        queue.add(source);
        while (!queue.isEmpty()) {
            var v = queue.poll();
            visited[v] = true;
            var adj = IntStream.range(0, size)
                    .boxed()
                    .map(u -> Pair.of(u, graph[v][u]))
                    .filter(p -> p.right != 0 && !visited[p.left])
                    .map(p -> p.left)
                    .collect(Collectors.toList());
            queue.addAll(adj);
        }
        return IntStream.range(0, size).filter(i -> visited[i]).boxed().collect(Collectors.toSet());
    }
}
