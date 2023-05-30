package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ChooseTilesFromBoardView;
import it.polimi.ingsw.Client.View.LastPlayerConnectedView;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class LastOneConnectedMsg extends S2CMessage{

    public String nickname;

    public LastOneConnectedMsg(String nickname){
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(new LastPlayerConnectedView(this));
        serverHandler.getClient().getCurrentView().notifyView();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            client.goToLastPlayerConnectedView(this);
            if(client.getCurrentView().getClass() == WaitingView.class)
                client.notifyView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
