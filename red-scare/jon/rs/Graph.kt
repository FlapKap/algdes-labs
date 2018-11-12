package rs

import edu.uci.ics.jung.graph.DirectedSparseMultigraph

class Graph (
        val graph: DirectedSparseMultigraph<Node, Edge>,
        val undirected: Boolean,
        val source: Node,
        val sink: Node
)