package rs

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow

/**
 * Wrapper for the EdmondsKarp-MaxFlow algorithm result.
 */
class MaxFlow(
        private val graph: Graph,
        edgeCapTransformer: (Edge) -> Number
) : EdmondsKarpMaxFlow<Node, Edge>(
        graph.graph,
        graph.source,
        graph.sink,
        { e: Edge? -> edgeCapTransformer(e!!) },
        mutableMapOf<Edge, Number>(),
        { Edge() }
) {
    init {
        evaluate()
    }

    fun flowGraph(): Graph {
        return Graph(flowGraph, graph.undirected, graph.source, graph.sink)
    }
}