package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * GoWaitingGUI msg: this message allows to make the transition to an idle view in GUI for the current player, who
 * should be waiting for the start of his turn.
 * @author Riccardo Lodelli
 */
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
