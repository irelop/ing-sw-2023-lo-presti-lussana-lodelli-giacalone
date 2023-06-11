package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.Remote;
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
        if(parent != null) {
            LoginView loginView = (LoginView) serverHandler.getOwner().getCurrentView();
            loginView.setLoginNicknameAnswer(this);
            serverHandler.getOwner().getCurrentView().notifyView();
        }
        else{
            LoginView loginView = new LoginView();

        }
    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            LoginView loginView = (LoginView) client.getCurrentView();
            loginView.setLoginNicknameAnswer(this);
            if(client.getCurrentView().getClass() == WaitingView.class)
                client.notifyView();
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
}

