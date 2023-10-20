package Pruebas;
public class Client extends Thread{
    String vectorMessage;
    String centroidMessage;
    String messageToServer;
    Client (String vectorMessage, String centroidMessage){
        this.vectorMessage = vectorMessage;
        this.centroidMessage = centroidMessage;
    }

    public void run(){

        // separar vectorMessage y almacenar en un arreglo de point
        String[] vectorParts = vectorMessage.split(" ");
        Point[] points = separarPuntos(vectorParts);

        String[] centroidParts = centroidMessage.split(" ");
        Point[] centroids = separarPuntos(centroidParts);
        /*for (Point point : points) {
            System.out.println(point);
        }

        for (Point centroid : centroids) {
            System.out.println(centroid);
        }*/
        process(points, centroids);
    }

    Point[] separarPuntos(String[] vectorParts) {
        int numVectores = vectorParts.length - 2;
        Point[] puntos = new Point[numVectores];
        for (int i = 2; i < numVectores + 2; i++) {
            String[] vectorPart = vectorParts[i].split("\\(");
            String name = vectorPart[0];
            String[] coordinates = vectorPart[1].split("\\)");
            String[] coordinatesParts = coordinates[0].split(",");
            double x = Double.parseDouble(coordinatesParts[0]);
            double y = Double.parseDouble(coordinatesParts[1]);
            puntos[i - 2] = new Point(name, x, y);
        }
        return puntos;
    }

    void process(Point[] points, Point[] centroids) {
        KMeansAlgorithm kMeansAlgorithm = new KMeansAlgorithm(points, centroids);
        kMeansAlgorithm.asignarPuntos();
        kMeansAlgorithm.actualizarCentroides();

        String message = "Resultado vector ";
        for (Point point : points) {
            message += point.name + "(" + point.x + "," + point.y + ")-" + point.cluster + ", ";
        }
        message += "/Resultado centroide ";
        for (Point centroid : centroids) {
            message += centroid.name + "(" + centroid.x + "," + centroid.y + ")-" + centroid.puntos + ", ";
        }

        System.out.println(message);
        //System.out.println("-----------------------------------------");
        messageToServer = message;
    }



    public String getMessage(){
        return this.messageToServer;
    }

}
