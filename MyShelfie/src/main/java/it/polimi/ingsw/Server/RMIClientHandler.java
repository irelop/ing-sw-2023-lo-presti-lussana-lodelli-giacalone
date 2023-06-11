package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.Model.GameRecord;
import it.polimi.ingsw.Server.Model.MyShelfie;

import java.rmi.RemoteException;


public class RMIClientHandler extends ClientHandler {

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


    @Override
    public void run(){
        boolean goOn = true;
        while(goOn){
            try{
                Thread.sleep(1000);
                getClientInterface().ping();
            }catch(Exception e){
                isConnected = false;
                if(getController() != null)
                    getController().shouldFinishTurn(this);
                System.out.println("RMI client disconnected");
                goOn = false;
            }
        }
    }

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
}
