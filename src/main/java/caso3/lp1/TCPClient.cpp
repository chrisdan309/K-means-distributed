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
const string serverIp = "127.0.0.1";
const int PORT = 8080;

int main() {
    char msg[1500]; 
    //configuamos el socket  
    //struct hostent* host = gethostbyname(serverIp.c_str());
    auto host = gethostbyname(serverIp.c_str());
    sockaddr_in sendSockAddr;

    memset((char*)&sendSockAddr, 0,sizeof(sendSockAddr));

    sendSockAddr.sin_family = AF_INET; 
    sendSockAddr.sin_addr.s_addr = inet_addr(inet_ntoa(*(struct in_addr*)*host->h_addr_list));
    sendSockAddr.sin_port = htons(PORT);

    int clientSd = socket(AF_INET, SOCK_STREAM, 0);
    // Intenta conectarse
    int status = connect(clientSd,(sockaddr*) &sendSockAddr, sizeof(sendSockAddr));
    if(status < 0) {
        cout<<"Error,no se encontro el servidor!"<<endl;
        return -1;
    }
    cout << "Conectado al servidor!" << endl;
    int bytesRead, bytesWritten = 0;
    struct timeval start1, end1;
    gettimeofday(&start1, NULL);
    while(true) {
        cout << "> ";
        string data;
        getline(cin, data);
        memset(&msg, 0, sizeof(msg));//limpia el buffer
        strcpy(msg, data.c_str());
        if(data == "exit") {
            send(clientSd, (char*)&msg, strlen(msg), 0);
            break;
        }
        bytesWritten += send(clientSd, (char*)&msg, strlen(msg), 0);
        cout << "Esperando respuesta del servidor.." << endl;
        memset(&msg, 0, sizeof(msg));//clear the buffer
        bytesRead += recv(clientSd, (char*)&msg, sizeof(msg), 0);
        if(!strcmp(msg, "exit")) {
            cout << "Servidor abandono la sesion" << endl;
            break;
        }
        cout << "Servidor: " << msg << endl;
    }
    gettimeofday(&end1, NULL);
    close(clientSd);
    cout << "********Sesion********" << endl;
    // cout << "Bytes written: " << bytesWritten << 
    // " Bytes read: " << bytesRead << endl;
    cout << "Duracion de conexion: " << (end1.tv_sec- start1.tv_sec) 
      << " s" << endl;
    cout << "Conexion cerrada" << endl;
    return 0;    
}