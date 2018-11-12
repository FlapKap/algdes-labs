package rs

data class Edge(
        val label: String,
        val from: Node,
        val to: Node
) {
    val adjacentToRed: Boolean = from.isRed || to.isRed
    val adjacentToSource: Boolean = from.isSource || to.isSource
    val adjacentToSink: Boolean = from.isSink || to.isSink

    constructor(): this(
            "",
            Node(false, "", false, false),
            Node(false, "", false, false)
    )
}
