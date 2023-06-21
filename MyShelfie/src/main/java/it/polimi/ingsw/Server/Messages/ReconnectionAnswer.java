package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.view.CLI.LoginView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

public class ReconnectionAnswer extends S2CMessage{


    public String msg;
    public String nickname;


    public ReconnectionAnswer(String msg,String nickname){
        this.msg = msg;
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        } else {
            serverHandler.getOwner().setNickname(nickname);     //re-setting the nickname
            LoginView loginView = (LoginView) serverHandler.getOwner().getCurrentView();
            loginView.setReconnectionAnswer(this);
            serverHandler.getOwner().getCurrentView().notifyView();
        }
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            } else {
                client.getOwner().setNickname(nickname);     //re-setting the nickname
                LoginView loginView = (LoginView) client.getOwner().getCurrentView();
                loginView.setReconnectionAnswer(this);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
