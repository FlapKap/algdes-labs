import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import org.junit.jupiter.api.Test
import rs.Edge
import rs.Graph
import rs.Node
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RedScareTest {

    private val gEx = run {
        val diGraph = DirectedSparseMultigraph<Node, Edge>()
        val nodes = listOf(
                Node(false, "0", isSource = true),
                Node(false, "1"),
                Node(false, "2"),
                Node(false, "3", isSink = true),
                Node(true, "4"),
                Node(true, "5"),
                Node(false, "6"),
                Node(true, "7")
        )
        nodes.forEach { diGraph.addVertex(it) }

        val edges = listOf(
                "0 -- 1",
                "1 -- 2",
                "2 -- 3",
                "0 -- 4",
                "4 -- 3",
                "0 -- 5",
                "5 -- 6",
                "6 -- 7",
                "7 -- 3"
        ).map { s ->
            val vertices = s
                    .replace(" ", "")
                    .split("--")
                    .map { n ->
                        Integer.parseInt(n)
                    }
            Pair(s, Pair(vertices.first(), vertices.last()))
        }

        edges.forEach {
            val from = nodes[it.second.first]
            val to = nodes[it.second.second]
            diGraph.addEdge(Edge(it.first, from, to), from, to)
            diGraph.addEdge(Edge(it.first.reversed(), to, from), to, from)
        }

        return@run Graph(diGraph, true, nodes[0], nodes[3])
    }

    /**
     * Return the length of a shortest s, t-path internally avoiding R.
     * To be precise, let P be the set of s, t-paths using no vertices from R
     * except maybe s and t themselves. Let l(p) denote the length of a
     * path p. Return min { l(p) : p \in P }. If no such path exists, return
     * ‘-1’. Note that if the edge st exists then the answer is 1, no matter
     * the colour of s or t. In Gex, the answer is 3 (because of the path 0, 1, 2, 3.)
     */
    @Test
    fun noneTest() {
        val noReds = gEx.copy(edgeFilter = { !it.adjacentToRed })
        val shortestPath = DijkstraShortestPath(noReds.graph).getPath(noReds.source, noReds.sink)

        assertEquals(3, shortestPath.size)
    }

    /**
     * Return ‘true’ if there is a path from s to t that includes at least
     * one vertex from R. Otherwise, return ‘false.’ In Gex, the answer is ‘yes’
     * (in fact, two such paths exist: the path 0, 4, 3 and the path 0, 5, 6, 7, 3).
     */
    @Test
    fun someTest() {
        val onlyReds = gEx.copy(edgeFilter = { it.adjacentToRed })
        val shortestPath = DijkstraShortestPath(onlyReds.graph).getPath(onlyReds.source, onlyReds.sink)

        assertTrue(shortestPath.isNotEmpty(), "There should be some path from source to sink")
    }

    /**
     * Return ‘true’ if there is a path from s to t that alternates
     * between red and non-red vertices. To be precise, a path v1, . . . , vl is
     * alternating if for each i \in { 1, . . . , l - 1 }, exactly one endpoint of the
     * edge (v_i -- v_i+1) is red. Otherwise, return ‘false.’ In Gex, the answer is
     * ‘yes’ (because of the path 0, 5, 6, 7, 3.)
     */
    @Test
    fun alternatingTest() {
        val alternating = gEx.copy(edgeFilter = { it.from.isRed xor it.to.isRed })
        val shortestPath = DijkstraShortestPath(alternating.graph).getPath(alternating.source, alternating.sink)

        //Lets try removing the shortest path, just for fun

        val withoutShortest = alternating.copy(edgeFilter = { !shortestPath.contains(it) })
        val longerPath = DijkstraShortestPath(
                withoutShortest.graph).getPath(withoutShortest.source, withoutShortest.sink
        )

        val vertices = longerPath
                .flatMap { listOf(it.from, it.to) }
                .fold(emptySet<Node>()) { acc, node -> acc.plusElement(node) }
                .toList()

        for (i in 0 until vertices.size - 1) {
            assertTrue(
                    vertices[i].isRed xor vertices[i + 1].isRed,
                    "In edge ${vertices[i].label} -- ${vertices[i + 1].label} exactly one endpoint must be red!"
            )
        }

        //However, it should be enough to just take the shortest path.
    }
}