package caso2.lp1;

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

    String centroidMessage = "";
    String vectorMessage = "";
    public void sendMessageToTCPServer(String message) {
        // clientCount = 6;
        if (clientCount == 0) {
            System.out.println("No hay clientes conectados");
            System.exit(0);
        }
        String[] parts = message.split("/");
        vectorMessage = parts[0];
        centroidMessage = parts[1];
        String[] partesVector = vectorMessage.split(" ");
        int contadorVectores = 0;
        for (String parte : partesVector) {
            if (parte.contains("(")) {
                contadorVectores++;
            }
        }

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
                        if (cad.contains(aux)) {
                            cadenaConPuntos.append(cad).append(" ");
                        }
                    }
                    // Ingresa cada punto del vector
                    indice++;
                }
            }
            String cadenaAEnviar = cadenaConPuntos.append("/").append(centroidMessage).toString();
            System.out.print(cadenaAEnviar);
            connectedClients[parte].sendMessage(message);
            System.out.println();
        }
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

