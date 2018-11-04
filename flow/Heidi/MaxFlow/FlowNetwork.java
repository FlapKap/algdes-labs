import java.util.LinkedList;

public class FlowNetwork {
	private final int V;
	private int E;
	private LinkedList<FlowEdge>[] adj;
	
	public FlowNetwork(int V) {
		this.V = V;
		this.E = 0;
		adj = (LinkedList<FlowEdge>[]) new LinkedList[V];
		for(int v = 0; v < V; v++)
			adj[v] = new LinkedList<FlowEdge>();
	}
	
	public void addEdge(FlowEdge e) {
		int v = e.either();
		adj[v].add(e);
		E++;
	}
	
	public int V() {
		return V;
	}
	
	public int E() {
		return E;
	}
	
	public Iterable<FlowEdge> adj(int v){
		return adj[v];
	}
	
}
