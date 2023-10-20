package Pruebas;

public class Point {
    public String name;
    public double x,y;
    public int cluster;
    public double puntos;

    public Point(double x, double y){
        this.name = "";
        this.x = x;
        this.y = y;
        this.cluster = -1;
        this.puntos = 0;
    }
    public Point(String name, double x, double y){
        this.name = name;
        this.x = x;
        this.y = y;
        this.cluster = -1;
        this.puntos = 0;
    }

    public Point Add(Point x, Point y){
        return new Point(x.x + y.x, x.y + y.y);
    }

    public Point Scalar(double k, Point p){
        return new Point(k * p.x, k * p.y);
    }


    @Override
    public String toString() {
        return name + "(" + x + ", " + y + ")";
    }
}