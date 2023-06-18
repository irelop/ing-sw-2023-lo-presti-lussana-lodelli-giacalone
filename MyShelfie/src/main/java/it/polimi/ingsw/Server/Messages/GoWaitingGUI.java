package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class GoWaitingGUI extends S2CMessage {

    @Override
    public void processMessage(ServerHandler serverHandler) {
        if(serverHandler.getOwner().gui){
            serverHandler.getOwner().getStageManager().loadNextStage(this, "waiting.fxml");
        }
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            if(client.getOwner().gui){
                client.getOwner().getStageManager().loadNextStage(this, "waiting.fxml");
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}