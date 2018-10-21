import java.io.*;
import java.util.*;

public class MaxFlow_ArraySolution {
	
	public static int[][] readFile(String filepath) throws IOException  {
		
		try {
			Scanner sc = new Scanner(new File(filepath));
			int n = sc.nextInt();
			int G[][] = new int[n][n];
			
			while(sc.hasNextLine()) 
				if(sc.nextLine().trim().contains("DESTINATIONS"))
					break;
	
			sc.nextInt();
			while(sc.hasNext()) {
				int startNode = sc.nextInt();
				int endNode = sc.nextInt();
				int capacity = sc.nextInt();
				if(capacity == -1) capacity = Integer.MAX_VALUE;
				G[startNode][endNode] = capacity;
				G[endNode][startNode] = capacity;
			}
			sc.close();
			return G;
		}
		catch(FileNotFoundException e){
            System.err.println("The specified file could not be found: " + filepath);
            System.exit(1);
            return null;
		}
	}
	
	public static boolean bfs(int rG[][], int P[], LinkedList<Integer> A, LinkedList<Integer> B) {
		
		int n = rG.length;
		int s = 0;
		int t = n-1;
		boolean visited[] = new boolean[n]; 
		
		for(int i=0;i<n;i++)
			visited[i]=false;
		
		LinkedList<Integer> queue = new LinkedList<Integer>(); 
		
		queue.add(s);
		visited[s] = true;
		P[s]=-1;
		
		while(queue.size()!=0) {
			int u = queue.poll();
			
			for(int v=0;v<n;v++) {
				if(visited[v] == false && rG[u][v] > 0) {
					queue.add(v);
					P[v] = u;
					visited[v] = true;
				}
			}
		}
		
		if(visited[t] == false) {
			for(int i=0;i<n;i++) {
				if(visited[i] == true)
					A.add(i);
				else
					B.add(i);
			}
		}

		return (visited[t] == true); 
	}
	
	public static int FordFulkerson(int G[][]) {
		int n = G.length, u, v, b = 0, f = 0;
		int s = 0;
		int t = n-1;
		int P[] = new int[n];
		LinkedList<Integer> A = new LinkedList<Integer>(); 
		LinkedList<Integer> B = new LinkedList<Integer>(); 
		
		// Create residual graph
		int rG[][] = new int[n][n];
		for(int i=0; i<n; i++) 
			for(int j=0; j<n; j++)
				rG[i][j] = G[i][j];
		
		while(bfs(rG,P, A, B)) {
			b = Integer.MAX_VALUE; 
			
			// Find minimum capacity in path (bottleneck)
			for(v=t; v!=s; v=P[v]) {
				u = P[v];
				b = Math.min(b, rG[u][v]);
			}
			
			// Update residual graph
			for(v=t; v!=s; v=P[v]) {
				u = P[v];
				rG[u][v] = rG[u][v] - b;
				rG[v][u] = rG[v][u] + b;
			}
			f = f + b;
		}
		
		minCut(G, A, B);

		return f;
	}
	
	public static void minCut(int G[][], LinkedList<Integer> A, LinkedList<Integer> B) { 
		
		for(Integer a: A)
			for(Integer b: B)
				if(G[a][b]>0)
					System.out.println(a + " " + b + " " + G[a][b]);

	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Please give a single file name as argument.");
		    System.exit(1);
		}
		
		//long StartTime = System.nanoTime();
		
		int G[][] = readFile(args[0]);
		int maxFlow = FordFulkerson(G);
		
		System.out.println();
		System.out.println("Max flow: " + maxFlow);
		
		 //long EndTime = System.nanoTime();
		 //System.out.println("Time (milliseconds): "+ (EndTime - StartTime)/1000000);
		
	}

}
