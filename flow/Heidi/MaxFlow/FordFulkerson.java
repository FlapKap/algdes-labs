import java.util.LinkedList;

public class FordFulkerson {
	private boolean[] visited;
	private FlowEdge[] edgeTo;
	private int maxFlow = 0;
	
	public FordFulkerson(FlowNetwork G, int s, int t) {
		while(hasAugmentingPath(G, s, t)) {
			int bottleneck = Integer.MAX_VALUE;
			for(int v = t; v != s; v = edgeTo[v].other(v)) 
				bottleneck = Math.min(bottleneck, edgeTo[v].residualCapacityTo(v));
			
			for(int v = t; v != s; v = edgeTo[v].other(v)) 
				edgeTo[v].updateResidualCapacityTo(v, bottleneck);
			
			maxFlow = maxFlow + bottleneck;
		}
		minCut(G);
	}
	
	public int maxFlow() {
		return maxFlow;
	}
	
	private void minCut(FlowNetwork G) {
		for(int v=0; v<G.V(); v++) 
			if(visited[v]) 
				for(FlowEdge e : G.adj(v)) 
					if(!visited[e.other(v)]) 
						System.out.println(v + " " + e.other(v) + " " + e.capacity());
		
	}
	
	private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
		visited = new boolean[G.V()];
		edgeTo = new FlowEdge[G.V()];
		LinkedList<Integer> queue = new LinkedList<Integer>(); 
		
		visited[s] = true;
		queue.add(s);
		
		while(!queue.isEmpty() && !visited[t]) {
			int v = queue.pop();
			for(FlowEdge e : G.adj(v)) {
				int w = e.other(v);
				
				if(e.residualCapacityTo(w) > 0 && !visited[w]) {
					edgeTo[w] = e;
					visited[w] = true;
					queue.add(w);
				}
			}
		}
		return visited[t];
	}

}
