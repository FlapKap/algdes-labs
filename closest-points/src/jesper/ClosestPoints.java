import java.util.*;
import java.io.*;
import java.math.*;
import java.util.regex.Pattern;

class Point {
	String name;
	double x;
	double y;
	
	public Point(String name, double x,double y) {
		this.name = name;
		this.x = x;
		this.y = y;
		
	}
	
	public static void printPoints(ArrayList<Point> p){
		for (int i = 0; i < p.size(); i++)
			System.out.println(p.get(i).name +  ": (x" + i + " , y" + i + ") = (" + p.get(i).x + " , " + p.get(i).y + ")");
	}
	
	public static Comparator<Point> xcomparator= new Comparator<Point>() {
		public int compare (Point s1, Point s2) {
			double s1x = s1.x;
			double s2x = s2.x;
			return Double.compare(s1x,s2x);
		}	
	};
	
	public static Comparator<Point> ycomparator= new Comparator<Point>() {
		public int compare (Point s1, Point s2) {
			double s1y = s1.y;
			double s2y = s2.y;
			return Double.compare(s1y,s2y);
		}
	};
}

public class ClosestPoints {
// Opret klasse (points) med x og y variabel til koordinater.
	//indlæs dette i en arrayList.

//afstand mellem to punkter
	private static double distance(Point a,Point b) {
		 return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y,2)); 
	}
	
	
	public static double shortestPoint(ArrayList<Point> px){
		int shorta = 0;
		int shortb = 1;
		double shortd = distance(px.get(0),px.get(1));
		for (int i = 0; i < px.size() - 1; i++) {
			for (int j = i + 1; j < px.size(); j++) {
			if (distance(px.get(i),px.get(j)) < shortd) {
				shortd = distance(px.get(i),px.get(j));
				shorta = i;
				shortb = i+1;
				//ArrayList<Point> shortPoint = px.get(i); 
			}
    	}
		}
		return shortd; //shortPoint;
	}
	
	public static ArrayList<Point> shortestPointFifteen(ArrayList<Point> px, int b){
		double shortd = distance(px.get(0),px.get(1));
		ArrayList<Point> shortPoint = new ArrayList<>();
		shortPoint.add(px.get(0));
		shortPoint.add(px.get(1));
		for (int i = 0; i < px.size() - 1; i++) {
			for (int j = i + 1; j < px.size(); j++) { 
			if (distance(px.get(i),px.get(j)) < shortd & i != j) {
				shortd = distance(px.get(i),px.get(j));
				shortPoint.set(0, px.get(i));
				shortPoint.set(1, px.get(j));
			}
			if (j == i + b)
				break;
			}
    	}
		return shortPoint; //shortPoint;
	}
	
	
	public static ArrayList<Point> closestPairRec(ArrayList<Point> Px,ArrayList<Point> Py) {
		double d;
		double xstar;
		ArrayList<Point> closestQ = new ArrayList<Point>();
		ArrayList<Point> closestR = new ArrayList<Point>();
		ArrayList<Point> closestS = new ArrayList<Point>();
		
		
		if (Px.size() <= 3) {
			return shortestPointFifteen(Px,Px.size());
		}
		else {
			ArrayList<Point> Qx = new ArrayList<Point>(Px.subList(0, Px.size()/2));
			ArrayList<Point> Rx = new ArrayList<Point>(Px.subList(Px.size()/2, Px.size()));
			ArrayList<Point> Qy = new ArrayList<Point>(Px.subList(0, Px.size()/2));
			ArrayList<Point> Ry = new ArrayList<Point>(Px.subList(Px.size()/2, Px.size()));
			
			Collections.sort(Qy , Point.ycomparator);
			Collections.sort(Ry , Point.ycomparator);
			
			closestQ = closestPairRec(Qx,Qy);
			closestR = closestPairRec(Rx,Ry);
			
			d = Math.min(shortestPoint(closestQ),
					shortestPoint(closestR));
			xstar = Qx.get(Qx.size()-1).x;
						
			ArrayList<Point> S = new ArrayList<Point>();
			for (int i = 0; i < Py.size();i++) {
				if (Py.get(i).x >= xstar - d & Py.get(i).x <= xstar + d )
					S.add(Py.get(i));
			}
			closestS = shortestPointFifteen(S,15);
			if (shortestPoint(closestS) < d) {
				return closestS;
			}
			if (shortestPoint(closestQ) < shortestPoint(closestR))
			return closestQ;
			else 
			return	closestR;			
		}
		}
	
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		ArrayList<Point> points = new ArrayList<Point>();
		
		// reading data from file
		Scanner scan = new Scanner(System.in);
		while (scan.hasNextLine()) {
			if (scan.nextLine().contains("NODE_COORD_SECTION"))
			{break;}
		}
			
			while (scan.hasNextLine()) {
				final String lineFromFile  = scan.nextLine().trim();
				if (lineFromFile.contains("EOF"))
						break;
				points.add(new Point(lineFromFile.split("\\s+")[0],
						Double.parseDouble(lineFromFile.split("\\s+")[1]),
						Double.parseDouble(lineFromFile.split("\\s+")[2])));
			
		}
		
// Sortering:
ArrayList<Point> pointsx = (ArrayList<Point>) points.clone();
ArrayList<Point> pointsy = (ArrayList<Point>) points.clone();


Collections.sort(pointsy , Point.ycomparator);
Collections.sort(pointsx , Point.xcomparator);

//Point.printPoints(closestPairRec(pointsx,pointsy)); 
System.out.println(shortestPoint(closestPairRec(pointsx,pointsy)));
long stopTime = System.nanoTime();
System.out.println(stopTime - startTime);
	}
}

