package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.RemoteInterface;

import java.io.IOException;

public abstract class ServerHandler implements Runnable{
    /*
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public Socket server;
    */
    public Client owner;

    private RemoteInterface remoteServer;


    public ServerHandler(Client owner,RemoteInterface remoteServer) {
        this.owner = owner;
        this.remoteServer = remoteServer;
    }


    //event loop che riceve i messaggi dal server e li processa
    private void handleClientConnection() throws IOException{}

    public Client getOwner(){return owner;}


    //funzione per mandare i mesaggi dal client al server
    public void sendMessageToServer(C2SMessage msg){}

    public void stop(){}


    public RemoteInterface getRemoteServer() {
        return remoteServer;
    }
}
