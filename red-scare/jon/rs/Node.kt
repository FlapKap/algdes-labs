package rs

/**
 * The class for representing nodes.
 */
data class Node(val isRed: Boolean, val label: String, val isSource: Boolean = false, val isSink: Boolean = false)