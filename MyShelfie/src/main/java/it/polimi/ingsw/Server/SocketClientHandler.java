package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.controller.GameRecord;
import it.polimi.ingsw.Server.controller.MyShelfie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SocketClientHandler class: this class represents the handler of a possible client connected to the server using the
 *                            socket technology.
 *
 * @author Andrea Giacalone
 */
public class SocketClientHandler extends ClientHandler{
    //the socket used for communicate with the connected client.
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    private final Socket client;
    private final AtomicBoolean shouldStop = new AtomicBoolean(false);


    public SocketClientHandler(Socket client, MyShelfie game){
        super(game, null);
        this.client = client;
        setIsRMI(false);
    }
    public SocketClientHandler(Socket client){
        super(null);
        this.client = client;
        setIsRMI(false);
    }

    public SocketClientHandler(Socket client, GameRecord gameRecord){
        super(null, gameRecord);
        this.client = client;
        setIsRMI(false);
    }


    @Override
    public void run() {
        try {
            outputStream = new ObjectOutputStream(client.getOutputStream());
            inputStream = new ObjectInputStream(client.getInputStream());
        } catch (IOException ex) {
            System.out.println("[SKT] Error: failed to open connection to " + client.getInetAddress());
            return;
        }

        System.out.println("[" + client.getInetAddress() + "] "+"now connected through SKT");

        try {
            handleClientConnection();
        } catch (IOException ex) {
            //detection possible disconnection notifying the controller
            this.isConnected = false;
            if(getController() != null)
                getController().shouldFinishTurn(this);
            System.out.println("[SKT] Client" + client.getInetAddress() + " connection dropped");
        }

        try {
            isConnected= false;
            client.close();
        } catch (IOException ex) {
            stop();
            System.out.println("[SKT] Error: failed to close connection with client" + client.getInetAddress());
        }
    }

    /**
     * OVERVIEW: this method manages an event loop in order to receive messages from the client and processes them.
     * @throws IOException: Input/Output exception
     */
    void handleClientConnection() throws IOException {

        try{
            boolean stop = false;
            while (!stop) {
                try {
                    Object nextAction = inputStream.readObject();
                    C2SMessage messageReceived = (C2SMessage) nextAction;
                    messageReceived.processMessage(this);
                } catch (IOException e) {
                    if (shouldStop.get()) {
                        stop = true;
                    }else throw e;
                }
            }
        }catch(ClassNotFoundException | ClassCastException e){
            System.out.println("[SKT] Error: invalid stream from the client");
        }
    }

    /**
     * OVERVIEW: this method allows to send a message from the server to the client using the Java serialization approach.
     * @param message: the message to be forwarded to the client.
     */
    @Override
    public void sendMessageToClient(S2CMessage message) {
        try {
            outputStream.flush();
            outputStream.writeObject(message);

        }catch (IOException ex){
            System.out.println("Failed to send the message to client" + client.getInetAddress());
            ex.printStackTrace();
        }
    }

    public void stop() {
        shouldStop.set(true);
        try {
            InetAddress i = client.getInetAddress();
            client.shutdownInput();
            System.out.println("[SKT] Connection closed with "+i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean isConnected() {
        return isConnected;
    }
}






