package it.polimi.ingsw.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHandler implements Runnable{
    private Socket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Client owner;
    private AtomicBoolean shouldStop = new AtomicBoolean(false);

    ServerHandler(Socket server, Client owner){
        this.server = server;
        this.owner = owner;
    }

    //questa run fa andare l'event loop che servirà per ricevere i pacchetti in arrivo dal server
    @Override
    public void run(){
        try{
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
        }catch(IOException e){
            System.out.println("can't open the connection to: "+server.getInetAddress());
            owner.setTrueTerminate();
            return;
        }

        try{
            handleClientConnection();
        }catch(IOException e){
            System.out.println("server "+ server.getInetAddress()+" connection dropped");
        }

        try{
            server.close();
        }catch (IOException e){
            owner.setTrueTerminate();
        }

    }


    //event loop che riceve i messaggi dal server e li processa
    //continua...
}
