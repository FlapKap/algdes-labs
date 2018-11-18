import edu.princeton.cs.algs4.Digraph;
import util.jon.Pair;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph {
    public final boolean directed;

    public final Node source;
    public final Node sink;

    public final Map<Node, Integer> mapping = new HashMap<>();

    Set<Node> nodes;
    Map<Node, Set<Node>> edges;

    public Graph(Set<Node> nodes, Map<Node, Set<Node>> edges, Node source, Node sink, boolean directed) {
        this.nodes = nodes;
        this.edges = edges;
        this.directed = directed;
        this.source = source;
        this.sink = sink;
        int i = 0;
        for (Node node : nodes) {
            mapping.put(node, i++);
        }

        if (this.source == null || this.sink == null) {
            // No path should exists if source nor sink is a part of the set
            throw new IllegalArgumentException("Source or sink are not connected with edges");
        }
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public Graph copy(Predicate<Node> filter) {
        var filteredNodes = nodes.stream()
                .filter(filter)
                .collect(Collectors.toSet());

        // var filteredNodes = new ArrayList<Node>(nodes);
        // filteredNodes.removeIf(filter.negate());

        Map<Node, Set<Node>> filteredEdges = new HashMap<>(edges);
        filteredEdges.keySet().removeIf(filter.negate());
        filteredEdges.values().forEach(s -> s.removeIf(filter.negate()));

        return new Graph(filteredNodes, filteredEdges, source, sink, directed);
    }

    public Graph copy(BiPredicate<Node, Node> edgeFilter) {
        Supplier<Stream<Pair<Node, Node>>> filteredEdgesAsPairs = () -> edges.entrySet().stream()
                .flatMap(entry -> {
                    var from = entry.getKey();
                    return entry.getValue().stream()
                            .map(to -> Pair.of(from, to));
                })
                .filter(p -> edgeFilter.test(p.left, p.right));
        var filteredNodes = nodes.stream()
                .filter(n -> filteredEdgesAsPairs.get()
                        .anyMatch(p -> p.left.equals(n) || p.right.equals(n))
                )
                .collect(Collectors.toSet());
        var filteredEdges = filteredEdgesAsPairs.get()
                .reduce(
                        new HashMap<Node, Set<Node>>(),
                        (acc, edge) -> {
                            if (!acc.containsKey(edge.left)) {
                                acc.put(edge.left, new HashSet<>());
                            }
                            acc.get(edge.left).add(edge.right);
                            return acc;
                        },
                        (l, r) -> {
                            for (Map.Entry<Node, Set<Node>> entry: r.entrySet()) {
                                if (l.containsKey(entry.getKey())) {
                                    l.get(entry.getKey()).addAll(entry.getValue());
                                } else {
                                    l.put(entry.getKey(), entry.getValue());
                                }
                            }
                            return l;
                        }
                );
        return new Graph(filteredNodes, filteredEdges, source, sink, directed);
    }

    public Digraph asDigraph() {
        var graph = new Digraph(nodes.size());

        edges.forEach((key, value) -> {
            for (Node v : value) {
                graph.addEdge(mapping.get(key), mapping.get(v));
                if (directed) {
                    graph.addEdge(mapping.get(v), mapping.get(key));
                }
            }
        });
        return graph;
    }
}

/*
1. Parse file
    -> Get data on size
    -> node info (red, and crap)
    -> edges (string -> string)

2. Filter nodes based on task and return a resulting graph
    - Hmmm...
    - Filter nodes based on red property
    - Construct DiGraph with edges that fit the filter
*/