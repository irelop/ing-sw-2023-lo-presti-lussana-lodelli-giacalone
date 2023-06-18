package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class ReconnectionAnswer extends S2CMessage{

    //public boolean canConnect;
    public String msg;
    public String nickname;

    /*public ReconnectionAnswer(boolean canConnect) {
        this.canConnect = canConnect;
    }*/
    public ReconnectionAnswer(String msg,String nickname){
        this.msg = msg;
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getOwner().setNickname(nickname);     //re-setting the nickname
        LoginView loginView = (LoginView) serverHandler.getOwner().getCurrentView();
        loginView.setReconnectionAnswer(this);
        serverHandler.getOwner().getCurrentView().notifyView();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            client.getClient().setNickname(nickname);     //re-setting the nickname
            LoginView loginView = (LoginView) client.getClient().getCurrentView();
            loginView.setReconnectionAnswer(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
