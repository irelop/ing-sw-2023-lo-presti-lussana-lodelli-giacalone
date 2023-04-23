package it.polimi.ingsw.Server;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Server.Messages.S2CMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * ClientHandler class: an abstract class which represents the client inside the server.
 *
 * @author Andrea Giacalone
 */

public abstract class ClientHandler implements Runnable {
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;
    Client client;

    //private Controller controller;

    void handleClientConnection() throws IOException {

    }

    void sendMessageToClient(S2CMessage message) throws IOException {

    }

    //public Controller getController(){ return this.controller;}

}


