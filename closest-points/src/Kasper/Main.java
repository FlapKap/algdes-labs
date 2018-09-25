package Kasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


class Pair<A> {
    final A a;
    final A b;

    Pair(A a, A b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}

class Point {

    final double x;
    final double y;
    final String name;


    Point(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", name='" + name + '\'' +
                '}';
    }
}


class Algorithm {

    static double distance(Pair<Point> p) {
        return distance(p.a, p.b);
    }

    private static double distance(Point a, Point b){
        return Math.sqrt(Math.pow( a.x - b.x , 2) + Math.pow(a.y - b.y, 2));
    }

    static Pair<Point> ClosestPair(List<Point> points) {
        List<Point> px = new ArrayList<>(points);
        px.sort(Comparator.comparingDouble(p -> p.x));

        List<Point> py = new ArrayList<>(points);
        px.sort(Comparator.comparingDouble(p -> p.y));

        return ClosestPairRec(px, py);
    }

    private static Pair<Point> ClosestPairRec(List<Point> px, List<Point> py) {
        if (px.size() == py.size() && px.size() <= 3) {

            Pair<Point> closest = null;
            double closest_dist = Double.POSITIVE_INFINITY;

            for (int i = 0; i < px.size(); i++) {
                for (int j = i + 1; j < px.size(); j++) {
                    var a = px.get(i);
                    var b = px.get(j);
                    double dist = distance(a, b);
                    if (dist < closest_dist) {
                        closest_dist = dist;
                        closest = new Pair<>(a, b);
                    }
                }
            }
            return closest;
        } else {
            var qx = px.subList(0, px.size() / 2);
            var rx = px.subList(px.size() / 2, px.size());

            var qy = py.subList(0, py.size() / 2);
            var ry = py.subList(py.size() / 2, py.size());

            var q = ClosestPairRec(qx, qy);
            var r = ClosestPairRec(rx, ry);

            var delta = Math.min(distance(q), distance(r));
            var xstar = qx.get(qx.size() - 1).x;

            var sy = px.stream()
                    .filter(p -> p.x < (xstar + delta) && p.x > (xstar - delta))
                    .sorted(Comparator.comparingDouble(p -> p.y))
                    .collect(Collectors.toList());

            Pair<Point> closest = null;
            var closest_dist = Double.MAX_VALUE;

            for (var i = 0; i < sy.size(); i++) {
                for (var j = i+1; j < Math.min(sy.size(), i + 15); j++) {
                    var a = sy.get(i);
                    var b = sy.get(j);
                    var dist = distance(a, b);
                    if (dist < closest_dist) {
                        closest_dist = dist;
                        closest = new Pair<>(a, b);
                    }
                }
            }

            if (closest_dist < delta) {
                return closest;
            } else if (distance(q) < distance(r)) {
                return q;
            } else {
                return r;
            }
        }
    }
}



public class Main {
    
    static List<Point> parser(Scanner sc){
        List<Point> output = new ArrayList<>();
        sc.useDelimiter("\\n");

        while (sc.hasNext()){
            var line = sc.next().trim();
            if (line.matches("\\D+.*") && line.isEmpty()){
                continue;
            }

            var linesc = new Scanner(line);
            var name = linesc.next();
            var x = linesc.nextDouble();
            var y = linesc.nextDouble();
            output.add(new Point(x, y, name));
            linesc.close();
        }
        sc.close();
        return output;
    }
    public static void main(String[] args) {
        List<Point> points = new ArrayList<>();
        switch (args.length){
            case 0:
                System.err.println("need input");
                System.exit(1);
                break;
            case 1:
                try {
                    points = parser(new Scanner(new File(args[0])));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                points = parser(new Scanner(System.in));
        }

        var result = Algorithm.ClosestPair(points);
        System.out.println(result);
        System.out.println(Algorithm.distance(result));
    }
}
