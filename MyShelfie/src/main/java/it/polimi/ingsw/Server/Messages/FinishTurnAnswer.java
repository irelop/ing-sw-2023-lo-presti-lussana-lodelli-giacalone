package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ChooseTilesFromBoardView;
import it.polimi.ingsw.Client.View.GameIsEndingView;
import it.polimi.ingsw.Client.View.View;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class FinishTurnAnswer extends S2CMessage{

    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(null);
        serverHandler.getClient().getCurrentView().notifyView();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            client.transitionToView(null);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
