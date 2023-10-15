package caso1.lp1;

import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    protected int clientCount = 0;
    public static final int SERVER_PORT = 4455;
    private final OnMessageReceived messageListener;
    private final TCPServerThread[] connectedClients = new TCPServerThread[10];
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }
    public OnMessageReceived getMessageListener() {
        return this.messageListener;
    }

    // Send message to all connected clients

    String centroidMessage = "";
    public void sendMessageToTCPServer(String message) {
        // clientCount = 6;
        if (clientCount == 0) {
            System.out.println("No hay clientes conectados");
            System.exit(0);
        }
        String[] parts = message.split("/");
        String vectorMessage = parts[0];
        centroidMessage = parts[1];
        // System.out.println("Vector: " + vectorMessage);
        // System.out.println("Centroide: " + centroidMessage);

        String[] partesVector = vectorMessage.split(" ");
        int contadorVectores = 0;
        for (String parte : partesVector) {
        //    System.out.println("Parte: " + parte);
            if (parte.contains("(")) {
                contadorVectores++;
            }
        }
        // System.out.println("Numero de vectores: " + contadorVectores);

        int elementosPorParte = contadorVectores / clientCount;
        int elementosExtras = contadorVectores % clientCount;

        int indice = 1;
        // Repartición de vectores
        for (int parte = 1; parte <= clientCount; parte++) {
            System.out.print("Enviado al cliente " + parte + ": ");
            int elementosEnEstaParte = elementosPorParte;

            if (elementosExtras > 0) {
                elementosEnEstaParte++;
                elementosExtras--;
            }
            StringBuilder cadenaConPuntos = new StringBuilder("enviar vector ");
            for (int i = 0; i < elementosEnEstaParte; i++) {

                if (indice <= contadorVectores) {
                    String aux = "a" + indice + "(";
                    for (String cad : partesVector) {
                        //    System.out.println("Parte: " + parte);
                        if (cad.contains(aux)) {
                            cadenaConPuntos.append(cad).append(" ");
                        }
                    }
                    // Ingresa cada punto del vector
                    //System.out.print(conjunto[indice] + " ");
                    indice++;
                }

            }
            String cadenaAEnviar = cadenaConPuntos.append("/").append(centroidMessage).toString();
            System.out.print(cadenaAEnviar);
            connectedClients[parte].sendMessage(message);
            System.out.println();
        }


       /* int inicio = 0;
        System.out.println("Numero de vectores: " + contadorVectores);
        for (int i = 0; i < clientCount; i++) {
            String mensaje = "enviar ";
            for (int j = inicio; j < inicio + tamanoParte; j++) {
                mensaje += partes[j] + " ";
            }
            inicio += tamanoParte;
            if (elementosExtras > 0) {
                mensaje += partes[inicio] + " ";
                inicio++;
                elementosExtras--;
            }
            System.out.println("Mensaje: " + mensaje);
            connectedClients[i+1].sendMessage(mensaje);
        }*/





        /*String[] parts = message.split(" ");
        String command = parts[0];
        String polynomial = parts[1];
        double a = Double.parseDouble(parts[2]);
        double b = Double.parseDouble(parts[3]);
        int n = Integer.parseInt(parts[4]);
        double interval = (b - a) / clientCount;
        int numIntervals = n / clientCount;

        for (int i = 1; i <= clientCount; i++) {
            String start = Double.toString(a + (i - 1) * interval);
            String end = Double.toString(a + i * interval);
            String num = Integer.toString(numIntervals);

            if (i == clientCount) {
                end = Double.toString(b);
                num = Integer.toString(n - (clientCount - 1) * numIntervals);
            }

            String newMessage = command + " " + polynomial + " " + start + " " + end + " " + num;

            connectedClients[i].sendMessage(newMessage);
            System.out.println("Enviado al Cliente " + i);
        }*/
        /*for (int i = 1; i <= clientCount; i++) {
            //connectedClients[i].sendMessage(message);
            System.out.println("Enviado al Cliente " + i);
        }*/
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
            while (true) {
                Socket client = serverSocket.accept();
                clientCount++;
                System.out.println("Clientes totales: " + clientCount);
                connectedClients[clientCount] = new TCPServerThread(client, this, clientCount, connectedClients);
                connectedClients[clientCount].start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void stopServer() {
        for (int i = 1; i <= clientCount; i++) {
            connectedClients[i].stopClient();
        }
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}

