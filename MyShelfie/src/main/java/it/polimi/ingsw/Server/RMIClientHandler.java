package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.Model.GameRecord;
import it.polimi.ingsw.Server.Model.MyShelfie;

import java.rmi.RemoteException;

/**
 * RMIClientHandler class: an implementation of the abstract class ClientHandler in order to manage the network functions
 * used in an RMI communication.
 * @author Irene Lo Presti, Andrea Giacalone
 */

public class RMIClientHandler extends ClientHandler {
    @Deprecated
    public RMIClientHandler(MyShelfie game, RemoteInterface remoteClient){
        super(game, remoteClient);
        setIsRMI(true);
    }

    public RMIClientHandler(RemoteInterface remoteClient){
        super(remoteClient);
        setIsRMI(true);
    }

    public RMIClientHandler(RemoteInterface remoteClient, GameRecord gameRecord){
        super(remoteClient, gameRecord);
        setIsRMI(true);
    }

    /**
     * OVERVIEW: this method checks the connection with the client sending periodically ping signals and eventually
     * manages its disconnection.
     */
    @Override
    public void run(){
        boolean goOn = true;
        while(goOn){
            try{
                Thread.sleep(1000);
                getClientInterface().ping();
            }catch(Exception e){
                goOn = false;
                stop();
            }
        }
    }

    /**
     * OVERVIEW: this method checks the connection status of the client.
     * @return true: if it's still connected, false: otherwise.
     */
    @Override
    public boolean isConnected() {
        if(isConnected){
            try {
                isConnected = getClientInterface().isClientConnected();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return isConnected;
    }

    @Override
    public void sendMessageToClient(S2CMessage message){
        try {
            getClientInterface().sendMessageToClient(message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        isConnected = false;
        if(getController() != null)
            getController().shouldFinishTurn(this);
        System.out.println("RMI client disconnected");
    }
}
