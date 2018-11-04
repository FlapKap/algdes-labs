import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Please give a single file name as argument.");
		    System.exit(1);
		}
		
		FlowNetwork G = FileReader.ReadNetwork(args[0]);
		int maxFlow = new FordFulkerson(G, 0, G.V()-1).maxFlow();
		//long StartTime = System.nanoTime();
		
		System.out.println();
		System.out.println("Max flow: " + maxFlow);
		
		 //long EndTime = System.nanoTime();
		 //System.out.println("Time (milliseconds): "+ (EndTime - StartTime)/1000000);
		
	}
}
