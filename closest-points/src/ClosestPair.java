import java.util.*;
import java.io.*;

public class ClosestPair {
	
	static class point {
		String name;
		double x, y;
		
		public point(String name, double x, double y) {
			this.name = name;
			this.x = x;
			this.y = y;
		}
		
		public double distance(point p2) {
			return Math.sqrt(Math.pow(p2.x - x, 2) + Math.pow(p2.y - y, 2));
		}
	}

	
	static class MinDistancePoints{
		point p0;
		point p1;
		double distance;

		public MinDistancePoints(point p0, point p1) {
			this.p0 = p0;
			this.p1 = p1;
		}
		
		public void printPoints() {
			//System.out.println("Point: "+p0.name+" = (" + p0.x + "," + p0.y + ") and "+p1.name+" = (" + p1.x + "," + p1.y + ")");
			System.out.println(p0.distance(p1));
		}
		
		public double distance() {
			return p0.distance(p1);
		}
	}
	
	
	public static MinDistancePoints FindMinDistancePoints(ArrayList<point> P) {
		MinDistancePoints closestPair = new MinDistancePoints(null,null);
		double dmin, d;
		
		dmin = P.get(0).distance(P.get(1));
		closestPair.p0 = P.get(0);
		closestPair.p1 = P.get(1);
		
		for(int i=0;i<P.size()-1;i++) {
			for(int j=1; j<Math.min(P.size()-i, 15); j++) {
				d = P.get(i).distance(P.get(i+j));
				if(d<dmin) {
					dmin = d;
					closestPair.p0 = P.get(i);
					closestPair.p1 = P.get(i+j);
				}
			}
		}
		return closestPair;
	}
	
	
	public static void sort_by_x(ArrayList<point> P) {
		 P.sort(Comparator.comparingDouble(o -> o.x));
	}
	
	public static void sort_by_y(ArrayList<point> P) {
		 P.sort(Comparator.comparingDouble(o -> o.y));
	}
	
	
	public static MinDistancePoints ClosestPairRec(ArrayList<point> Px, ArrayList<point> Py) {
		MinDistancePoints closestPair = new MinDistancePoints(null,null);
		MinDistancePoints closestPair_Q = new MinDistancePoints(null,null);
		MinDistancePoints closestPair_R = new MinDistancePoints(null,null);
		MinDistancePoints closestPair_S = new MinDistancePoints(null,null);
		
		ArrayList<point> Qx = new ArrayList<>();
		ArrayList<point> Qy = new ArrayList<>();
		ArrayList<point> Rx = new ArrayList<>();
		ArrayList<point> Ry = new ArrayList<>();
		ArrayList<point> Sy = new ArrayList<>();
	
		double xStar, delta;
		int n = Px.size();
		
		if(n <= 3) {
			closestPair = FindMinDistancePoints(Px);
			return closestPair;
		}
		else {
			// Construct Qx, Qy, Rx, Ry
			for(int i=0;i<n/2;i++) {
				Qx.add(Px.get(i));
				Qy.add(Px.get(i));
			}
			for(int i=n/2;i<n;i++) {
				Rx.add(Px.get(i));
				Ry.add(Px.get(i));
			}
			sort_by_y(Qy);
			sort_by_y(Ry);

			closestPair_Q = ClosestPairRec(Qx,Qy);
			closestPair_R = ClosestPairRec(Rx,Ry);
			
			delta = Math.min(closestPair_Q.distance(), closestPair_R.distance());
			xStar = Px.get(n/2-1).x;
			
			for(int i=0;i<n;i++) 
				if(Py.get(i).x >= xStar-delta & Py.get(i).x <= xStar+delta)
					Sy.add(Py.get(i));
			
			if(Sy.size() > 1) {
				closestPair_S = FindMinDistancePoints(Sy);
				if(closestPair_S.distance() < delta) 
					return closestPair_S;
			}
			if(closestPair_Q.distance() < closestPair_R.distance()) 
				return closestPair_Q;
			else 
				return closestPair_R;
		}
	}
	
	
	public static ArrayList<point> readFile(String filepath) throws IOException{
		Scanner sc = new Scanner(new File(filepath));
		ArrayList<point> P = new ArrayList<>();
		String[] pointData = new String[3];
		String str;

		while(sc.hasNextLine()) 
			if(sc.nextLine().contains("NODE_COORD_SECTION")) 
				break;
			 
		while(sc.hasNext()) {
			str = sc.nextLine();
			if(str.contains("EOF"))
				break;
			String[] parts = str.split(" ");
			int j = 0;
			for(int i=0;i<parts.length;i++) 
				if(!"".equals(parts[i])) 
					pointData[j++] = parts[i];
			P.add(new point(pointData[0],Double.parseDouble(pointData[1]),Double.parseDouble(pointData[2])));
		}
		sc.close();
		return P;
	}
	
	
	 public static void main(String[] args) throws IOException {
		 
		 ArrayList<point> P = readFile(args[0]);
		 ArrayList<point> Px = new ArrayList<>(P);
		 ArrayList<point> Py = new ArrayList<>(P);
		 sort_by_x(Px);
		 sort_by_y(Py);
		 MinDistancePoints closestPair = ClosestPairRec(Px,Py);
		 closestPair.printPoints();
	}
}
