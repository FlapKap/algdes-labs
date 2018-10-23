package flow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ResidualGraphTest {

    /*
        A -10-> B -10-> C -5 -> E
          -8 -> D -8 -> E
     */

    /*
             B -----10---> C -----+
             ^                    |
             |                    |
             10                   5
             |                    |
        A +--+                    +--> E
             |                    |
             +---8--> D -----8----+


     */

    @Test
    void findNewPathInSmallGraph() {
        final var testGraph = new ResidualGraph(5);
        final var source = testGraph.source;
        final var sink = testGraph.sink;
        testGraph.addEdge(source, 1, 10);
        testGraph.addEdge(1, 2, 10);
        testGraph.addEdge(2, sink, 5);
        testGraph.addEdge(source, 3, 8);
        testGraph.addEdge(3, sink, 8);

        final Set<Integer> expectedMinCut = Set.of(0, 1, 2);

        final int expectedMaxFlow = 5 + 8; //The two bottlenecks combined.

        int actualMaxFlow = 0;

        var path = testGraph.findNewPath();
        testGraph.augmentByPath(path);
        actualMaxFlow += path.bottleneck;
        var path2 = testGraph.findNewPath();
        testGraph.augmentByPath(path2);
        actualMaxFlow += path2.bottleneck;
        var probablyNotAPath = testGraph.findNewPath();

        var actualMinCut = testGraph.sourceComponent();

        assertTrue(probablyNotAPath.nodes.isEmpty());
        assertEquals(expectedMaxFlow, actualMaxFlow);
        assertEquals(expectedMinCut, actualMinCut);
    }
}