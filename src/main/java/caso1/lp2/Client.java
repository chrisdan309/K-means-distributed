package caso1.lp2;

class Client {

    private final double[] sums = new double[40];
    private TCPClient tcpClient;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    void start() {
        new Thread(
                () -> {
                    tcpClient = new TCPClient("127.0.0.1", this::receiveFromClient);
                    tcpClient.run();
                }
        ).start();
    }

    void receiveFromClient(String input) {
        System.out.println("Mensaje recibido: " + input);
        String[] parts = input.split("/");
        String vectorMessage = parts[0];
        String centroidMessage = parts[1];

        // separar vectorMessage y almacenar en un arreglo de point
        String[] vectorParts = vectorMessage.split(" ");
        Point[] points = separarPuntos(vectorParts);

        String[] centroidParts = centroidMessage.split(" ");
        Point[] centroids = separarPuntos(centroidParts);
        for (Point point : points) {
            System.out.println(point);
        }

        for (Point centroid : centroids) {
            System.out.println(centroid);
        }
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

    void sendToClient(String message) {
        if (tcpClient != null) {
            tcpClient.sendMessage(message);
        }
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
        System.out.println("-----------------------------------------");
        sendToClient(message);
    }

}
