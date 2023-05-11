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
    /*
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public Socket server;
    */
    public Client owner;


    public ServerHandler(/*Socket server,*/Client owner) {
        /*
        try{
                output = new ObjectOutputStream(server.getOutputStream());
                input = new ObjectInputStream(server.getInputStream());
            }catch(IOException e){
                System.out.println("can't open the connection to: "+server.getInetAddress());
                owner.setTrueTerminate();
                return;
            }
            */


        this.owner = owner;
        //this.server = server;
    }


    //event loop che riceve i messaggi dal server e li processa
    private void handleClientConnection() throws IOException{}

    public Client getClient(){return owner;}


    //funzione per mandare i mesaggi dal client al server
    public void sendMessageToServer(C2SMessage msg){}

    public void stop(){}

}
