package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.S2CMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
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
    private void handleClientConnection() throws IOException{
        try{
            boolean stop = false;
            while (!stop) {
                try {
                    Object next = input.readObject();
                    S2CMessage command = (S2CMessage) next;
                    command.processMessage(this);
                } catch (IOException e) {
                    if (shouldStop.get()) {
                        stop = true;
                    }else throw e;
                }
            }
        }catch(ClassNotFoundException | ClassCastException e){
            System.out.println("invalid stream from server");
        }
    }

    public Client getClient(){return owner;}


    //funzione per mandare i mesaggi dal client al server
    public void sendMessageToServer(C2SMessage commandMsg){
        try {
            output.writeObject(commandMsg);
        } catch (IOException e) {
            System.out.println("Communication error");
            owner.terminate();
        }
    }

    public void stop() {
        shouldStop.set(true);
        try {
            server.shutdownInput();
        } catch (IOException e) {}
    }
}
