package caso2.lp1;

import java.util.Scanner;

import caso1.lp2.Point;

public class Server {

    private TCPServer tcpServer;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }


    void start() {
        System.out.println("El servidor se ha iniciado y está esperando conexiones...");

        new Thread(() -> {
            tcpServer = new TCPServer(message -> {
                synchronized (this) {
                    receiveFromServer(message);
                }
            });
            tcpServer.run();
        }).start();

        System.out.println("K means");
        System.out.print("Ejemplo:\nenviar vector a1(12,10), a2(1,3), a3(32,12)\n");
        System.out.print("enviar centroide c1(0,2), c2(0.2,12)\n");

        listening();

        tcpServer.stopServer();
        System.exit(0);

    }

    int clientCount = 0;
    String[] clientResponses = new String[20];
    boolean isVector = false;
    boolean isCentroide = false;
    String vectorMessage = "";
    String centroideMessage = "";

    // Recibe los mensajes de los clientes (Final)
    void receiveFromServer(String message) {
        if (message != null && !message.isEmpty()) {
            // Sumar la cantidad de cluster 1, 2, 3, etc
            // Update centroides
            // Si diferencia entre centroides es menor a 0.1, detener el programa
            // Sino, enviar mensaje a los clientes

            System.out.println("Mensaje recibido: " + message);

            String[] parts = message.split("/");
            String vectorMessage = parts[0];
            String centroidMessage = parts[1];
            String[] partesCentroides = centroidMessage.split(" ");
            int contadorCentroides = 0;
            for (String parte : partesCentroides) {
                if (parte.contains("(")) {
                    contadorCentroides++;
                }
            }
            if (message.trim().contains("Resultado")) {

                // Update Centroides
                clientResponses[clientCount] = message;
                clientCount++;

                // Llegaron todos los mensajes
                if (clientCount == this.tcpServer.clientCount) {
                    Point[] centroides = new Point[contadorCentroides];
                    // Llenar de point vacíos
                    for (int i = 0; i < contadorCentroides; i++) {
                        centroides[i] = new Point("c" + (i + 1), 0, 0);
                        centroides[i].puntos = 0;
                    }
                    // Recorre respuestas
                    for (String parte : clientResponses) {
                        if (parte == null) continue;
                        parts = parte.split("/");
                        centroidMessage = parts[1];
                        partesCentroides = centroidMessage.split(" ");
                        // Recorre partes de la cadena centroide
                        for (String centroideString : partesCentroides) {
                            if (centroideString.contains("(")) { // Si es un punto
                                String[] vectorPart = centroideString.split("\\(");
                                String name = vectorPart[0];
                                int contador = Integer.parseInt(name.substring(1)) - 1;
                                String[] coordinates = vectorPart[1].split("\\)");
                                String[] coordinatesParts = coordinates[0].split(",");
                                double x = Double.parseDouble(coordinatesParts[0]);
                                double y = Double.parseDouble(coordinatesParts[1]);
                                String pesoString = centroideString.split("-")[1];
                                pesoString = pesoString.substring(0, pesoString.length() - 1);
                                double peso = centroideString.split("-").length > 1 ? Double.parseDouble(pesoString) : 0;
                                Point aux = new Point(name, x, y);
                                aux.puntos = peso;
                                aux = aux.Scalar(peso, aux);
                                centroides[contador] = centroides[contador].Add(centroides[contador], aux);
                                centroides[contador].puntos += peso;
                            }
                        }
                    }
                    // Dividir centroides entre su peso
                    for (int i = 0; i < centroides.length; i++) {
                        centroides[i] = centroides[i].Scalar(1 / centroides[i].puntos, centroides[i]);
                    }

                    /*System.out.println("Centroides:");
                    for (Point centroide : centroides) {
                        System.out.println(centroide);
                    }*/
                    String oldCentroid = tcpServer.centroidMessage;
                    String[] centroidParts = oldCentroid.split(" ");
                    Point[] oldCentroidPoints = new Point[contadorCentroides];
                    for (int i = 2; i < contadorCentroides + 2; i++) {
                        String[] vectorPart = centroidParts[i].split("\\(");
                        String name = vectorPart[0];
                        String[] coordinates = vectorPart[1].split("\\)");
                        String[] coordinatesParts = coordinates[0].split(",");
                        double x = Double.parseDouble(coordinatesParts[0]);
                        double y = Double.parseDouble(coordinatesParts[1]);
                        oldCentroidPoints[i - 2] = new Point(name, x, y);
                    }
                    /*System.out.println("Old Centroides:");
                    for (Point centroide : oldCentroidPoints) {
                        System.out.println(centroide);
                    }*/
                    // Verificar si el cambio es menor a 0.1 con la norma 2
                    double error = getError(centroides, oldCentroidPoints);

                    if (error > 0.1) {
                        System.out.println("Error: " + error);
                        System.out.println("Reenvia mensaje");
                        // enviar centroide c1(0,2), c2(0.2,12)
                        centroidMessage = "enviar centroide ";
                        for (int i = 0; i < centroides.length; i++) {
                            if (i == centroides.length - 1) {
                                centroidMessage += "c" + (i + 1) + "(" + centroides[i].x + "," + centroides[i].y + ")";
                            } else {
                                centroidMessage += "c" + (i + 1) + "(" + centroides[i].x + "," + centroides[i].y + "), ";
                            }
                        }
                        //System.out.println(centroidMessage);
                        String messageToSend = tcpServer.vectorMessage + "/" + centroidMessage;
                        tcpServer.centroidMessage = centroidMessage;
                        //System.out.println(messageToSend);
                        // Reiniciar variables
                        clientCount = 0;
                        System.out.println("--------------------------------------------------");
                        sendToServer(messageToSend);
                    } else {
                        System.out.println("Error: " + error);
                        System.out.println("Termina");
                        // Separar vectormessage
                        String[] vectorParts = vectorMessage.split(" ");
                        int numVectores = vectorParts.length - 2;
                        Point[] puntos = new Point[numVectores];
                        for (int i = 2; i < numVectores + 2; i++) {

                            String[] vectorPart = vectorParts[i].split("\\(");
                            String name = vectorPart[0];
                            String[] coordinates = vectorPart[1].split("\\)");
                            String[] coordinatesParts = coordinates[0].split(",");
                            double x = Double.parseDouble(coordinatesParts[0]);
                            double y = Double.parseDouble(coordinatesParts[1]);
                            //get number of cluster
                            String clusterString = vectorParts[i].split("-")[1];
                            clusterString = clusterString.substring(0, clusterString.length() - 1);
                            int cluster = vectorParts[i].split("-").length > 1 ? Integer.parseInt(clusterString) : -1;
                            puntos[i - 2] = new Point(name, x, y);
                            puntos[i - 2].cluster = cluster;
                        }

                        // Imprimir puntos de cada centroide sus puntos asociados
                        System.out.println("\nPuntos por centroide:");
                        for (int i = 0; i < centroides.length; i++) {
                            System.out.println("Centroide " + (i + 1) + ": " + centroides[i]);
                            for (Point punto : puntos) {
                                if (punto.cluster == i) {
                                    System.out.println(punto);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static double getError(Point[] centroides, Point[] oldCentroidPoints) {
        double error = 0;
        for (int i = 0; i < centroides.length; i++) {
            error += Math.sqrt(Math.pow(centroides[i].x - oldCentroidPoints[i].x, 2) + Math.pow(centroides[i].y - oldCentroidPoints[i].y, 2));
        }
        return error;
    }

    // Envia los mensajes a los cliente
    void sendToServer(String message) {
        if (message != null) {
            if (message.trim().contains("enviar")) {
                if (message.trim().contains("vector")) {
                    vectorMessage = message;
                    isVector = true;
                } else if (message.trim().contains("centroide")) {
                    centroideMessage = message;
                    isCentroide = true;
                } else {
                    System.out.println("El mensaje no contiene la palabra 'vector' o 'centroide'");
                }

                if (tcpServer != null && isVector && isCentroide) {
                    message = vectorMessage + "/" + centroideMessage;
                    tcpServer.sendMessageToTCPServer(message);
                }
            } else {
                System.out.println("El mensaje no contiene la palabra 'enviar'");
            }
        }
    }

    void listening() {
        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            command = scanner.nextLine();
            sendToServer(command);
        } while (!command.equals("salir"));
    }
}
