package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class ReconnectionAnswer extends S2CMessage{

    public boolean canConnect;

    public ReconnectionAnswer(boolean canConnect) {
        this.canConnect = canConnect;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        LoginView loginView = (LoginView) serverHandler.getOwner().getCurrentView();
        loginView.setReconnectionAnswer(this);
        serverHandler.getOwner().getCurrentView().notifyView();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            LoginView loginView = (LoginView) client.getClient().getCurrentView();
            loginView.setReconnectionAnswer(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
