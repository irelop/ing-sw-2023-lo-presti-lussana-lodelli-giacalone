package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.S2CMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SocketServerHandler class: an implementation of the abstract class ServerHandler in order to manage the network functions
 *                            used in a socket communication.
 */

public class SocketServerHandler extends ServerHandler {
    public ObjectInputStream input;
    public ObjectOutputStream output;
    private final Socket server;
    private final AtomicBoolean shouldStop = new AtomicBoolean(false);

    public SocketServerHandler(Socket server, Client owner){
       super(owner);
       this.server = server;
        try{
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
        }catch(IOException e){
            System.out.println("[SKT] Error: can't open the connection to: "+server.getInetAddress());
            owner.setTrueTerminate();
        }
    }

    @Override
        public void run(){
        try{
            handleClientConnection();
        }catch(IOException e){
            System.out.println("[SKT] Server "+ server.getInetAddress()+" connection dropped");
            System.exit(0);
        }

        try{
            server.close();
        }catch (IOException e){
            owner.setTrueTerminate();
        }

    }


    /**
     * OVERVIEW: this method manages the event loop in order to deserialize the messages received by the server socket
     *           and to process them.
     * @throws IOException: exception thrown when an error occurs during serialization.
     */
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
            System.out.println("[SKT] Invalid stream from server");
        }
    }
    @Override
    public void sendMessageToServer(C2SMessage msg){
        try {
            output.flush();     //flushing the output stream associated to the socket
            output.writeObject(msg);    //serializing the message

        } catch (IOException e) {
            System.out.println("[SKT] Communication error: problems in sending message to the server");
            owner.setTrueTerminate();
        }
    }

    public void stop() {
        shouldStop.set(true);   //notifying the handling method that it should stop in the next loop ASAP
        try {
            server.shutdownInput();
            System.out.println("[SKT] Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
