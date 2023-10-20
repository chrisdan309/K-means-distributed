#include <iostream>
#include <string>
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <netdb.h>
#include <sys/uio.h>
#include <sys/time.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <fstream>
#include "KMeans.h"
using namespace std;
#define PORT 8080
int main() {
    const int port = PORT;
    char msg[200];
    sockaddr_in servAddr;
    bzero((char*)&servAddr, sizeof(servAddr));
    servAddr.sin_family = AF_INET;
    servAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servAddr.sin_port = htons(PORT);
    int serverSd = socket(AF_INET, SOCK_STREAM, 0);
    if(serverSd < 0) {
        cerr << "Error al establecer el socket del servidor" << endl;
        exit(0);
    }
    //Vincular el socket a la direccion local
    int bindStatus = bind(serverSd, (struct sockaddr*) &servAddr, sizeof(servAddr));
    if(bindStatus < 0) {
        cerr << "Error al vincular el socket a la direccion actual\n";
        exit(0);
    }
    std::cout << "Esperando la conexion del cliente...\n";

    listen(serverSd, 5);

    sockaddr_in newSockAddr;
    socklen_t newSockAddrSize = sizeof(newSockAddr);

    int newSd = accept(serverSd, (sockaddr *)&newSockAddr, &newSockAddrSize);
    if(newSd < 0)
    {
        cerr << "Error al aceptar la solicitud del cliente!\n";
        exit(1);
    }
    std::cout << "Conectado con cliente!\n";
    struct timeval start1, end1;
    gettimeofday(&start1, NULL);
    int bytesRead, bytesWritten = 0;
    string nombreArchivo = "input.txt";
    string output_dir = "output.txt";
    //fstream salida(nombreArchivo.c_str());
    fstream archivo(nombreArchivo.c_str(),fstream::in| fstream::out);
    while(1){
        std::cout << "Esperando respuesta de cliente...\n";
        memset(&msg, 0, sizeof(msg));//clear the buffer
        bytesRead += recv(newSd, (char*)&msg, sizeof(msg), 0);
        if(!strcmp(msg, "exit")){
            std::cout << "Cliente ha cerrado la sesion\n";
            break;
        }
        archivo << msg << "\n";
        std::cout << "Cliente: " << msg <<" recibido\n";
        std::cout << "> ";
        string data = "OK";
        //getline(cin, data);
        memset(&msg, 0, sizeof(msg)); //clear the buffer
        strcpy(msg, data.c_str());
        if(data == "exit") {
            //send to the client that server has closed the connection
            send(newSd, (char*)&msg, strlen(msg), 0);
            break;
        }            //Envia el mensaje al cliente
        bytesWritten += send(newSd, (char*)&msg, strlen(msg), 0);
        }
    archivo.close();
    // archivo.open("input.txt");
    // string line;
    // while(getline(archivo,line)){
    //     std::cout << line <<"\n";
    // }
    gettimeofday(&end1, NULL);
    close(newSd);
    close(serverSd);
    std::cout << "********Sesion********\n";
    //std::cout << "Bytes written: " << bytesWritten << " Bytes read: " << bytesRead << endl;
    std::cout << "Tiempo de sesion: " << (end1.tv_sec - start1.tv_sec) << "s\n";
    std::cout << "Conexion cerrada...\n";
    cout << "------------------------------\n";
    archivo.open("input.txt");
    // string line;
    // while(getline(archivo,line)){
    //     std::cout << line <<"\n";
    // }
    int pointId = 1;
    vector<Point> all_points;
    string line;
    int K = 2;
    while (getline(archivo, line))
    {
        Point point(pointId, line);
        all_points.push_back(point);
        pointId++;
    }
    
    archivo.close();
    cout << "\nDatos recuperados exitosamente!\n";
    // Return if number of clusters > number of points
    if ((int)all_points.size() < K)
    {
        cout << "Error: Number of clusters greater than number of points.\n";
        return 1;
    }
    // Running K-Means Clustering
    int iters = 100;
    for (int i = 1; i <= all_points.size(); i++) {
        KMeans kmeans(i, iters, output_dir);
        kmeans.run(all_points);
    }
    //cout << filesystem::current_path();
    return 0; 

}