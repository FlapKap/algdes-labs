package rs

data class Edge(
        val label: String,
        val adjacentToRed: Boolean,
        val adjacentToSource: Boolean,
        val adjacentToSink: Boolean
)