package it.polimi.ingsw.Server;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.Model.GameRecord;
import it.polimi.ingsw.Server.Model.MyShelfie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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
    protected GameRecord gameRecord;

    public ClientHandler(MyShelfie game, RemoteInterface client){
        this.game = game;
        this.client = client;
        this.isConnected = true;
    }

    public ClientHandler(RemoteInterface client){
        this.client = client;
        this.isConnected = true;
    }
    public ClientHandler(RemoteInterface client, GameRecord gameRecord){
        this.client = client;
        this.isConnected = true;
        this.gameRecord = gameRecord;
    }

    public void setGame(MyShelfie game) {
        this.game = game;
    }

    public void setGameFromGameRecord(){
        this.game = gameRecord.getGame();
    }

    public GameRecord getGameRecord(){
        return this.gameRecord;
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
    public void setIsConnected(boolean value){
        this.isConnected = value;
    }

    public abstract boolean isConnected();

}


