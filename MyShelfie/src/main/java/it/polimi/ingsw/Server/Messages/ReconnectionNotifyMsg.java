package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.view.CLI.LastPlayerConnectedView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import java.rmi.RemoteException;

/**
 * ReconnectionNotifyMsg: this message allows to notify the last connected player, if he/she exists, of the reconnection
 * of a player previously connected to the game. After that, the game can continue.
 * @author Irene Lo Presti, Andrea Giacalone
 */
public class ReconnectionNotifyMsg extends S2CMessage{
    public String nickname;

    public ReconnectionNotifyMsg(String nickname){
        this.nickname = nickname;
    }
    @Override
    public void processMessage(ServerHandler serverHandler) {
        if (serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        } else {
            LastPlayerConnectedView lastPlayerConnectedView = (LastPlayerConnectedView) serverHandler.getOwner().getCurrentView();
            lastPlayerConnectedView.notifyView(nickname);
        }
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().getController().receiveAnswer(this);}
            else {
                LastPlayerConnectedView lastPlayerConnectedView = (LastPlayerConnectedView) client.getOwner().getCurrentView();
                lastPlayerConnectedView.notifyView(nickname);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
