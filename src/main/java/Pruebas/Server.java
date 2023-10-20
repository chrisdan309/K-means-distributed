package Pruebas;

import java.util.Scanner;

public class Server {


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Registra el tiempo de inicio
        Server server = new Server();
        Scanner scanner = new Scanner(System.in);
        int numVectors, numCentroids, numberClients;
        System.out.println("Ingrese el número de vectores: ");
        numVectors = scanner.nextInt();
        System.out.println("Ingrese el número de centroides: ");
        numCentroids = scanner.nextInt();
        System.out.println("Ingrese el número de clientes: ");
        numberClients = scanner.nextInt();

        String vectorMessage = server.generateVectors("vector",numVectors);
        String centroidMessage = server.generateVectors("centroide",numCentroids);

        System.out.println(vectorMessage);
        System.out.println(centroidMessage);
        Client[] client = new Client[numberClients];


        String[] partesVector = vectorMessage.split(" ");
        int elementosPorParte = numVectors / numberClients;
        int elementosExtras = numVectors % numberClients;



        // REPITE
        while(true){
            int indice = 1;
            // Repartición de vectores
            for (int parte = 1; parte <= numberClients; parte++) {
                System.out.print("Enviado al cliente " + parte + ": ");
                int elementosEnEstaParte = elementosPorParte;

                if (elementosExtras > 0) {
                    elementosEnEstaParte++;
                    elementosExtras--;
                }
                String cadenaConPuntos = "enviar vector ";
                for (int i = 0; i < elementosEnEstaParte; i++) {
                    if (indice <= numVectors) {
                        String aux = "a" + indice + "(";
                        for (String cad : partesVector) {
                            if (cad.contains(aux)) {
                                cadenaConPuntos += cad + " ";
                            }
                        }
                        // Ingresa cada punto del vector
                        indice++;
                    }
                }
                client[parte-1] = new Client(cadenaConPuntos, centroidMessage);
                client[parte-1].start();
                System.out.println();
            }

            // Esperar a los clientes
            for(int i=0; i < client.length; i++){
                try {
                    client[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(client[0].getMessage());
            Point[] newCentroids = new Point[numCentroids];
            for (int i = 0; i < numCentroids; i++) {
                newCentroids[i] = new Point("c" + (i + 1), 0, 0);
                newCentroids[i].puntos = 0;
            }


            for (int i = 0; i < client.length; i++) {
                String parte = client[i].getMessage();
                if (parte == null) continue;
                String[] parts = parte.split("/");
                centroidMessage = parts[1];
                String[] partesCentroides = centroidMessage.split(" ");
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
                        newCentroids[contador] = newCentroids[contador].Add(newCentroids[contador], aux);
                        newCentroids[contador].puntos += peso;
                    }
                }
            }

            for (int j = 0; j < newCentroids.length; j++) {
                newCentroids[j] = newCentroids[j].Scalar(1 / newCentroids[j].puntos, newCentroids[j]);
            }

            double error = 0;

            if (error > 0.1) {
                System.out.println("Error: " + error);
                System.out.println("Reenvia mensaje");
                // enviar centroide c1(0,2), c2(0.2,12)
                centroidMessage = "enviar centroide ";
                for (int j = 0; j < newCentroids.length; j++) {
                    if (j == newCentroids.length - 1) {
                        centroidMessage += "c" + (j + 1) + "(" + newCentroids[j].x + "," + newCentroids[j].y + ")";} else {
                        centroidMessage += "c" + (j + 1) + "(" + newCentroids[j].x + "," + newCentroids[j].y + "), ";
                    }
                }
            } else {
                break;
            }
        }

        long endTime = System.currentTimeMillis(); // Registra el tiempo de finalización
        long executionTime = endTime - startTime; // Calcula la diferencia de tiempo
        System.out.println("Tiempo de ejecucion: " + executionTime + " milisegundos");

    }

    public String generateVectors(String tipo, int numVectors){
        String result = "enviar "+ tipo + " ";
        for (int i = 1; i <= numVectors; i++) {
            if(tipo.equals("vector"))
                result += "a" + i + "(";
            else
                result += "c" + i + "(";
            double x = (double)((int) (Math.random()*10000)/100.0);
            double y = (double)((int) (Math.random()*10000)/100.0);
            result += x + "," + y + ")";
            if (i < numVectors) {
                result += ", ";
            }
        }
        return result;
    }


}

