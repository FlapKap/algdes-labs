package com.wingcorp;

public class Point {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double Distance(Point other) {
        return Math.sqrt(
                Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2)
        );
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
