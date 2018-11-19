import java.io.InputStream;
import java.util.*;

public class GraphParser {

    public static Graph parse(InputStream stream) {
        var sc = new Scanner(stream);
        var n = sc.nextInt(); //num vertices
        var m = sc.nextInt(); //num edges
        var r = sc.nextInt(); //cardinality of R (whatever that means)
        String s = sc.next().trim(); //source
        String t = sc.nextLine().trim(); //sink
        //Read Vertices
        Map<String,Node> nodes = new HashMap<>();
        Map<Node, Set<Node>> adj = new HashMap<>();

        for (int i = 0; i < n; i++) {
            var line = sc.nextLine().trim();
            if(line.isEmpty()) continue;
            var isRed = line.endsWith(" *");
            var label = (isRed) ? line.substring(0, line.length() - 2) : line; //remove the ' *'
            nodes.put(
                    label,
                    new Node(isRed, label, label.equals(s), label.equals(t))
                    );
        }

        //Read edges
        boolean directed = false;

        for (int i = 0; i < m; i++) {
            var line = sc.nextLine().trim().split(" ");
            var first = nodes.get(line[0]);
            var second = nodes.get(line[2]);

            var edgeType = line[1];
            directed = edgeType.equals("->");

            adj.putIfAbsent(first, new HashSet<>());
            adj.get(first).add(second);

            if(!directed) {
                adj.putIfAbsent(second, new HashSet<>());
                adj.get(second).add(first);
            }
        }

        return new Graph(adj.keySet(), adj, nodes.get(s), nodes.get(t), directed, n, m, r);
    }

}
