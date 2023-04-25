package it.polimi.ingsw.Server;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.Model.Game;
import it.polimi.ingsw.Server.Model.MyShelfie;

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
    protected MyShelfie game;

    public ClientHandler(){

    }

    void handleClientConnection() throws IOException {
    }

    public void sendMessageToClient(S2CMessage message) {

    }

    public MyShelfie getController(){ return this.game;}

}


