package caso1.lp2;

public class Principal {
    public static void main(String[] args) {
        int k = 3;
        Point[] points = new Point[10];
        Point[] centroids = new Point[k];

        generatePoints(points, 10);
        generatePoints(centroids, k);
        //KMeansAlgorithm owo = new KMeansAlgorithm(points,centroids);
        //owo.Algorithm();


    }

    private static void generatePoints(Point[] points, int cantidad) {
        for(int i = 0; i < cantidad; i++){
            double x = Math.random()*20;
            double y = Math.random()*20;
            points[i] = new Point(x,y);
        }
    }





}
