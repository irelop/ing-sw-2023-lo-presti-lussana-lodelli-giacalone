package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.S2CMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * SocketClientHandler class: this class represents the handler of a possible client connected to the server using the
 *                            socket technology.
 *
 * @author Andrea Giacalone
 */
public class SocketClientHandler extends ClientHandler{
    //the socket used for communicate with the connected client.
    private Socket client;

    SocketClientHandler(Socket client){
        this.client = client;
        //super.controller = controller;
    }


    @Override
    public void run() {
        try {
            outputStream = new ObjectOutputStream(client.getOutputStream());
            inputStream = new ObjectInputStream(client.getInputStream());
        } catch (IOException ex) {
            System.out.println("Failed to open connection to " + client.getInetAddress());
            return;
        }

        System.out.println("Enstablished connection with client: " + client.getInetAddress());

        try {
            handleClientConnection();
        } catch (IOException ex) {
            System.out.println("client" + client.getInetAddress() + "connection dropped");
        }

        try {
            client.close();
        } catch (IOException ex) {
            System.out.println("Failed to close connection with client" + client.getInetAddress());
        }
    }

    /**
     * OVERVIEW: this method manages an event loop in order to receive messages from the client and processes them.
     * @throws IOException
     */
    @Override
    void handleClientConnection() throws IOException {
        try {
            while(true) {
                Object nextAction = inputStream.readObject();
                C2SMessage messageReceived = (C2SMessage) nextAction;

                messageReceived.processMessage(this);
            }
            }catch(ClassNotFoundException | ClassCastException e){
            System.out.println("Invalid stream from the client");
        }
    }

    /**
     * OVERVIEW: this method allows to send a message from the server to the client using the Java serialization approach.
     * @param message: the message to be forwarded to the client.
     * @throws IOException: if a problem with the output stream occurs.
     */
    @Override
    void sendMessageToClient(S2CMessage message) throws IOException {
        try {
            outputStream.writeObject((Object) message);
        }catch (IOException ex){
            System.out.println("Failed to send the message to client" + client.getInetAddress());
        }
    }
}






