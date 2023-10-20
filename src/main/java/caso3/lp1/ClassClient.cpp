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
class Server{
    private:
    const char* serverIp;
    const int PORT;
    char msg[100];
    struct hostent* host;
    sockaddr_in sendSockAddr;
    int clientSd;
    int status;
    int bytesRead,bytesWritten;
    timeval start,end;
    public:
    Server(): serverIp("127.0.0.1"),PORT(8080){

    }
    void init();
    int try_to_connect();
    int showtime();
    void processConnect();
    void details();
};
void Server::init(){
    this->serverIp = "127.0.0.1";
    this->host = gethostbyname(serverIp);
    memset((char*)&this->sendSockAddr,0,sizeof(this->sendSockAddr));
    this->sendSockAddr.sin_family = AF_INET;
    this->sendSockAddr.sin_addr.s_addr = inet_addr(inet_ntoa(*(struct in_addr*)this->host->h_addr_list));
    this->clientSd = socket(AF_INET,SOCK_STREAM,0);
    this->status = connect(this->clientSd,(sockaddr*)&this->sendSockAddr,sizeof(this->sendSockAddr));
}
int Server::try_to_connect(){
    if (this->status < 0) {
        cout<<"Error,no se encontro el servidor!\n";
        return -1;
    }
    cout << "Conectado al servidor!\n";
    return 0;
}
int Server::showtime(){
    gettimeofday(&this->start,nullptr);
    return this->start.tv_sec;
}
void Server::processConnect(){
    while (1) {
        cout << "> ";
        string data;
        getline(cin,data);
        memset(&this->msg, 0, sizeof(this->msg));
        strcpy(this->msg, data.c_str());
        if(data == "exit") {
            send(this->clientSd, (char*)&this->msg, strlen(this->msg), 0);
            break;
        }
        this->bytesWritten += send(this->clientSd, (char*)&this->msg, strlen(this->msg), 0);
        cout << "Esperando respuesta del servidor..\n";
        memset(&msg, 0, sizeof(msg));//clear the buffer
        bytesRead += recv(clientSd, (char*)&msg, sizeof(msg), 0);
        if(!strcmp(msg, "exit")) {
            cout << "Servidor abandono la sesion" << endl;
            break;
        }
        cout << "Servidor: " << msg << endl;
    }
}
void Server::details(){
    gettimeofday(&this->end,nullptr);
    close(this->clientSd);
    cout << "********Sesion********" << endl;
    // cout << "Bytes written: " << bytesWritten << 
    // " Bytes read: " << bytesRead << endl;
    cout << "Duracion de conexion: " << (this->end.tv_sec- this->start.tv_sec) << "s\n";
    cout << "Conexion cerrada\n";
}
int main() {
    Server myServer;
    myServer.init();
    myServer.try_to_connect();
    myServer.showtime();
    myServer.processConnect();
    myServer.details();
    return 0;
}