import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileReader {
	public static FlowNetwork ReadNetwork(String filepath) throws IOException {
		try {
			Scanner sc = new Scanner(new File(filepath));
			int V = sc.nextInt();
			FlowNetwork G = new FlowNetwork(V);
			
			for(int i=0; i<=V; i++)
				sc.nextLine();
	
			int E = sc.nextInt();
			
			for(int i=0; i<E; i++) {
				int v = sc.nextInt();
				int w = sc.nextInt();
				int capacity = sc.nextInt();
				if(capacity == -1) capacity = Integer.MAX_VALUE;
				G.addEdge(new FlowEdge(v,w,capacity));
				G.addEdge(new FlowEdge(w,v,capacity));
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
}
