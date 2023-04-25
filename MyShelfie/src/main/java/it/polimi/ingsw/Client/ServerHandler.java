package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.S2CMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ServerHandler implements Runnable{
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public Client owner;



    //event loop che riceve i messaggi dal server e li processa
    private void handleClientConnection() throws IOException{

    }

    public Client getClient(){return owner;}


    //funzione per mandare i mesaggi dal client al server
    public void sendMessageToServer(C2SMessage msg){

    }

    public void stop(){

    }

}
