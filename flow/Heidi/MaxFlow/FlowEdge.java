
public class FlowEdge {
	private final int v;
	private final int w;
	private final int capacity;
	private int residualCap = 0;
	
	public FlowEdge(int v, int w, int capacity) {
		this.v = v;
		this.w = w;
		this.capacity = capacity;
	}
	
	public int capacity() {
		return capacity;
	}
	
	public int either() {
		return v;
	}
	
	public int other(int vertex) {
		if(vertex == v) return w;
		else if (vertex == w) return v;
		else throw new RuntimeException("Vertex not correct");
	}
	
	public int residualCapacityTo(int vertex) {
		if(vertex == v) return residualCap;
		else if(vertex == w) return capacity - residualCap;
		else throw new RuntimeException("Vertex not correct");
	}
	
	public void updateResidualCapacityTo(int vertex, int bottleneck) {
		if(vertex == v) residualCap = residualCap - bottleneck;
		else if(vertex == w) residualCap = residualCap + bottleneck;
		else throw new RuntimeException("Vertex not correct");
	}
}
