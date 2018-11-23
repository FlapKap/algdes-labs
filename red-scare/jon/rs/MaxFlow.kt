package rs

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow
import java.util.concurrent.ThreadLocalRandom

/**
 * Wrapper for the EdmondsKarp-MaxFlow algorithm result.
 */
class MaxFlow(
        private val graph: Graph,
        private val source: Node,
        private val sink: Node,
        edgeCapTransformer: (Edge) -> Number
) : EdmondsKarpMaxFlow<Node, Edge>(
        graph.graph,
        graph.source,
        graph.sink,
        { e: Edge? -> edgeCapTransformer(e!!) },
        mutableMapOf<Edge, Number>(),
        { Edge("${ThreadLocalRandom.current().nextInt()}", Node(false, ""), Node(false, "")) }
) {
    init {
        evaluate()
    }

    fun flowGraph(): Graph {
        return Graph(flowGraph, graph.undirected, graph.source, graph.sink)
    }
}