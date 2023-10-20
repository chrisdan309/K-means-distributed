package Pruebas;

public class KMeansAlgorithm {
    public Point[] data;
    public Point[] centroides;

    public KMeansAlgorithm(Point[] data, Point[] centroides) {
        this.data = data;
        this.centroides = centroides;
    }

    public void asignarPuntos() {
        for (Point point : data){
            double minDistance = Double.MAX_VALUE;
            int cluster = -1;
            for (int i = 0; i < centroides.length; i++) {
                double distance = calculateDistance(point, centroides[i]);
                if (distance < minDistance) {
                    minDistance = distance;
                    cluster = i;
                }
            }
            centroides[cluster].puntos++;
            if(point.cluster != cluster){
                point.cluster = (cluster);
            }

        }
    }

    public void actualizarCentroides() {
        for (int i = 0; i < centroides.length; i++) {
            double sumX = 0;
            double sumY = 0;
            int count = 0;
            for (Point point : data) {
                if (point.cluster == i) {
                    sumX += point.x;
                    sumY += point.y;
                    count++;
                }
            }
            if (count > 0) {
                centroides[i].x = sumX / count;
                centroides[i].y = sumY / count;
            }
        }
    }

    static double calculateDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
}

