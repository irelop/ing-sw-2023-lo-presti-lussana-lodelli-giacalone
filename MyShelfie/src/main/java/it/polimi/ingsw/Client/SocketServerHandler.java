package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.S2CMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketServerHandler extends ServerHandler {
    //private Socket server;
    private AtomicBoolean shouldStop = new AtomicBoolean(false);

    public SocketServerHandler(Socket server, Client owner){
       super(server,owner);
    }

    //questa run fa andare l'event loop che servirà per ricevere i pacchetti in arrivo dal server
    @Override
        public void run(){
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

    //funzione per mandare i mesaggi dal client al server
    public void sendMessageToServer(C2SMessage msg){
        try {
            output.flush();
            output.writeObject(msg);

        } catch (IOException e) {
            System.out.println("Communication error");
            owner.setTrueTerminate();
        }
    }

    public void stop() {
        shouldStop.set(true);
        try {
            server.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
