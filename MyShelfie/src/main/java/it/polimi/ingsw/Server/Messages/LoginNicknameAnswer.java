package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.view.CLI.LoginView;
import it.polimi.ingsw.Client.view.CLI.WaitingView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

public class LoginNicknameAnswer extends S2CMessage{
    public LoginNicknameRequest getParent() {
        return parent;
    }

    public Status getNicknameStatus() {
        return nicknameStatus;
    }

    public enum Status{
        INVALID,
        ACCEPTED,
        FIRST_ACCEPTED,
        FULL_LOBBY;
    }

    LoginNicknameRequest parent;
    Status nicknameStatus;



    public LoginNicknameAnswer(LoginNicknameRequest parent, Status nicknameStatus){
        this.parent = parent;
        this.nicknameStatus = nicknameStatus;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        if (serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        } else {
            LoginView loginView = (LoginView) serverHandler.getOwner().getCurrentView();
            serverHandler.getOwner().setNickname(parent.getInsertedNickname());
            loginView.setLoginNicknameAnswer(this);
            serverHandler.getOwner().getCurrentView().notifyView();
        }
    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            if (client.getOwner().gui) {
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            } else {
                LoginView loginView = (LoginView) client.getCurrentView();
                loginView.setLoginNicknameAnswer(this);
                client.getOwner().setNickname(parent.getInsertedNickname());
                if(client.getCurrentView().getClass() == WaitingView.class)
                    client.notifyView();
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
}

