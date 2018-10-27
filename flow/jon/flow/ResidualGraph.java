package flow;


import util.jon.IntPair;

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

    private final int size;
    //               From     To, Capacity
    public final Map<Integer, List<IntPair>> graph;

    public final int source = 0;
    public final int sink;

    public ResidualGraph(int size) {
        this.size = size;
        graph = new TreeMap<>();
        sink = size - 1;
    }

    public ResidualGraph(Network network) {
        this.size = network.size;
        this.sink = network.sink;
        graph = new TreeMap<>();
        for (int i = source; i < size; i++) {
            graph.put(i, new LinkedList<>());
            for (int j = source; j < size; j++) {
                var cap = network.getCapacity(i, j);
                if (cap > 0) {
                    graph.get(i).add(IntPair.of(j, cap));
                }
            }
        }
    }

    public void addEdge(int from, int to, int capacity) {
        if (!graph.containsKey(from)) {
            graph.put(from, new LinkedList<>());
        }
        graph.get(from).add(IntPair.of(to, capacity));
    }

    private Path findPath(Path acc, int currentNode) {
        if (currentNode == sink) {
            return new Path(append(acc.nodes, currentNode), acc.bottleneck);
        }
        final var adj = graph.get(currentNode)
                .stream()
                .filter(p -> p.right > 0 && !acc.nodes.contains(p.left))
                /*
                    I can apply sorting for the highest capacity edges
                    to effectively cut down the number of total iterations.
                    But the sorting operation O(V * Log(V)) slows down
                    the program significantly (by about 5 seconds!).
                 */
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

    /**
     * Returns a new, unique path through the Residual Graph.
     *
     * @return A pair consisting of the vertices on the path and the capacity of the path.
     */
    public Path findNewPath() {
        return findPath(new Path(List.of(), Integer.MAX_VALUE), source);
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
            graph.put(v, graph.get(v).stream().map(p -> {
                if (p.left == u) {
                    return p.updateRight(cap -> cap - path.bottleneck);
                } else return p;
            }).collect(Collectors.toList()));
            graph.put(u, graph.get(u).stream().map(p -> {
                if (p.left == v) {
                    return p.updateRight(cap -> cap + path.bottleneck);
                } else return p;
            }).collect(Collectors.toList()));
        }
    }

    /**
     * Returns all nodes currently connected to the source.
     *
     * @return A set of all nodes connected to the source.
     */
    public List<Integer> sourceComponent() {
        var visited = new boolean[size];
        var queue = new LinkedList<Integer>();
        queue.add(source);
        while (!queue.isEmpty()) {
            var v = queue.poll();
            visited[v] = true;
            var adj = graph.get(v).stream()
                    .filter(p -> !visited[p.left])
                    .map(p -> p.left)
                    .collect(Collectors.toList());
            queue.addAll(adj);
        }
        return IntStream.range(0, size).filter(i -> visited[i]).boxed().collect(Collectors.toList());
    }
}
