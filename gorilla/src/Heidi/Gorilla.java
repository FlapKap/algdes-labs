import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Gorilla  {

	static class species {
		String name;
		String protein;
		
		public species(String name, String protein){
			this.name = name;
			this.protein = protein;
		}
	}
	
	static class costData {
		public int[][] costs;
		public String letter;
	}
	
	static class dataOutput{
		String X;
		String Y;
		int value;
	}
	
	public static ArrayList<species> readFile(String filepath) throws IOException {
		
		try {
			Scanner sc = new Scanner(new File(filepath));
			ArrayList<species> speciesData = new ArrayList<>();
			String name = "", protein = "";
			int RowCount = 0;
			
			while(sc.hasNextLine()) {
				String str = sc.nextLine().trim();
				
				if(">".equals(str.substring(0, 1))) {
					if(RowCount > 1)
						speciesData.add(new species(name, protein));
					String[] parts = str.split("\\s+");
					name = parts[0].replace(">", "");
					protein = "";
				}
				else {
					protein = protein + str;
				}
				RowCount++;
			}
			speciesData.add(new species(name, protein));
			sc.close();
			return speciesData;
		}
		catch(FileNotFoundException e){
            System.err.println("The specified file could not be found: " + filepath);
            System.exit(1);
            return null;
		}
	}
	
	public static costData readFileCosts() throws IOException{
		try {		
			Scanner sc = new Scanner(new File("C:/CodeRepository/algdes-labs/gorilla/data/BLOSUM62.txt"));
			String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ*";
			String str;
			String letter = "";
			int[][] costs = new int[24][24];
			int i = -1, j =0;
	
			while(sc.hasNextLine()) 
				if(!"#".equals(sc.nextLine().substring(0, 1))) 
					break;
		
			while(sc.hasNext()) {
				str = sc.next();
				if(alphabet.contains(str)) {
					i++;
					letter = letter + str;
					j = 0;
				}
				else {
					costs[i][j++] = Integer.parseInt(str);
				}
			}
			
			sc.close();
			costData output = new costData();
			output.costs = costs;
			output.letter = letter;
			return output;
		}
		catch(FileNotFoundException e){
            System.err.println("The specified file could not be found: C:/CodeRepository/algdes-labs/gorilla/data/BLOSUM62.txt");
            System.exit(1);
            return null;
		}
	}
	
	public static dataOutput SequenceAlignment(String X, String Y, costData costs){
		
		int m=X.length(), n=Y.length(), delta=-4;
		int[][] OPT = new int[n+1][m+1];
		int[][] T = new int[n+1][m+1];
			
		for(int i=1;i<=n;i++) {
			OPT[i][0] = i*delta;
			T[i][0] = 2;
		}
		for(int j=1;j<=m;j++) {
			OPT[0][j] = j*delta;
			T[0][j] = 3;
		}
		
		for(int j=1;j<=m;j++) {
			for(int i=1;i<=n;i++) {
				int LetterIndex_X = costs.letter.indexOf(X.charAt(j-1));
				int LetterIndex_Y = costs.letter.indexOf(Y.charAt(i-1));
				int alpha = costs.costs[LetterIndex_X][LetterIndex_Y];
				
				int A1 = alpha + OPT[i-1][j-1];
				int A2 = delta + OPT[i-1][j];
				int A3 = delta + OPT[i][j-1];

				if(A1 >= A2 && A1 >= A3) {
					OPT[i][j] = A1;
					T[i][j] = 1;
				}else if(A2 >= A1 && A2 >= A3) {
					OPT[i][j] = A2;
					T[i][j] = 2;
				}else if(A3 >= A1 && A3 >= A2) {
					OPT[i][j] = A3;
					T[i][j] = 3;
				}
			}
		}

		int i = n;
		int j = m;
		String Xalign = "";
		String Yalign = "";
		
		while(T[i][j] != 0) {
			
			if(T[i][j] == 1) {
				Xalign = X.charAt(--j) + Xalign;
				Yalign = Y.charAt(--i) + Yalign;
			}else if(T[i][j] == 2) {
				Xalign = "-" + Xalign;
				Yalign = Y.charAt(--i) + Yalign;
			}else if(T[i][j] == 3) {
				Xalign = X.charAt(--j) + Xalign;
				Yalign = "-" + Yalign;
			}
		}

		dataOutput output = new dataOutput();
		output.X = Xalign;
		output.Y = Yalign;
		output.value = OPT[n][m];
		
		return output;
	}
	
	 public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Please give a single file name as argument.");
		    System.exit(1);
		}
		 
		ArrayList<species> species = readFile(args[0]);
		costData costs = readFileCosts();

		for(int i=0;i<species.size();i++) {
			for(int j=i+1; j<species.size(); j++) {
				String X = species.get(i).protein;
				String Y = species.get(j).protein;
				dataOutput OPT = SequenceAlignment(X,Y,costs);
				System.out.println(species.get(i).name + "--" + species.get(j).name + ": " + OPT.value);
				System.out.println(OPT.X);
				System.out.println(OPT.Y);
			}
		}
	 }
}
