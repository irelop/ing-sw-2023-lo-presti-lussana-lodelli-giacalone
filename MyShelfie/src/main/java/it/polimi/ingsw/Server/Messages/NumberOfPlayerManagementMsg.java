package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class NumberOfPlayerManagementMsg extends S2CMessage{

    public String nickname;

    public NumberOfPlayerManagementMsg(String nickname){
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getOwner().transitionToView(new LoginView(this));
        serverHandler.getOwner().getCurrentView().notifyView();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            client.goToLoginView(this);
            if(client.getCurrentView().getClass() == WaitingView.class)
                client.notifyView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
