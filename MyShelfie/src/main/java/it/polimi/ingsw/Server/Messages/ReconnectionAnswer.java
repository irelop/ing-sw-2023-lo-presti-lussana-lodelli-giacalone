package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

public class ReconnectionAnswer extends S2CMessage{

    public ClientHandler clientHandler;

    public ReconnectionAnswer(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        LoginView loginView = (LoginView) serverHandler.getClient().getCurrentView();
        loginView.setReconnectionAnswer(this);
        serverHandler.getClient().getCurrentView().notifyView();

    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {

    }
}
