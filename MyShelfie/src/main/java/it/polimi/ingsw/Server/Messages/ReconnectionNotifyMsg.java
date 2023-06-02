package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LastPlayerConnectedView;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class ReconnectionNotifyMsg extends S2CMessage{
    @Override
    public void processMessage(ServerHandler serverHandler) {
        LastPlayerConnectedView lastPlayerConnectedView = (LastPlayerConnectedView) serverHandler.getClient().getCurrentView();
        lastPlayerConnectedView.notifyView();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            LastPlayerConnectedView lastPlayerConnectedView = (LastPlayerConnectedView) client.getClient().getCurrentView();
            lastPlayerConnectedView.notifyView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}