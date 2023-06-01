package it.polimi.ingsw.Server;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Server.Messages.S2CMessage;
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

    protected MyShelfie game;
    private boolean isRMI;
    private RemoteInterface client;
    protected boolean isConnected;

    public ClientHandler(MyShelfie game, RemoteInterface client){
        this.game = game;
        this.client = client;
        this.isConnected = true;
    }

    public void run(){}

    public MyShelfie getController(){ return this.game;}
    public void sendMessageToClient(S2CMessage message){};
    public void setIsRMI(boolean value){
        isRMI = value;
    }
    public void stop() {};
    public boolean getIsRMI(){
        return isRMI;
    }

    public RemoteInterface getClientInterface(){return client;}

    public abstract boolean isConnected();

}


