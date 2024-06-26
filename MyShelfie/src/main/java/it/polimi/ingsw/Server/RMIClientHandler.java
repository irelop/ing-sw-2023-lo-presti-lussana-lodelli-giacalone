package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.controller.GameRecord;
import it.polimi.ingsw.Server.controller.MyShelfie;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * RMIClientHandler class: an implementation of the abstract class ClientHandler in order to manage the network functions
 * used in an RMI communication.
 * @author Irene Lo Presti, Andrea Giacalone
 */

public class RMIClientHandler extends ClientHandler {
    private boolean goOn;

    public RMIClientHandler(RemoteInterface remoteClient){
        super(remoteClient);
        setIsRMI(true);
        goOn = true;
    }

    public RMIClientHandler(RemoteInterface remoteClient, GameRecord gameRecord){
        super(remoteClient, gameRecord);
        setIsRMI(true);
        goOn = true;
    }

    /**
     * OVERVIEW: this method checks the connection with the client sending periodically ping signals and eventually
     * manages its disconnection.
     */
    @Override
    public void run(){
        while(goOn){
            try{
                Thread.sleep(1000);
                if(!goOn) break;
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
                setIsConnected(false);
            }
        }
        return isConnected;
    }

    @Override
    public void sendMessageToClient(S2CMessage message){
        try {
            getClientInterface().sendMessageToClient(message);
        } catch (RemoteException e) {
            System.out.println("[RMI] Error: failed to send message to client");
        }
    }

    @Override
    public void stop() {
        goOn = false;
        isConnected = false;
        if(getController() != null)
            getController().shouldFinishTurn(this);
        System.out.println("[RMI] RMI client disconnected");
    }
}
