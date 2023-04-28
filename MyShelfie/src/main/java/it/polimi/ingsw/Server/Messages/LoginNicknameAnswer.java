package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Client.View.ObservableView;
import it.polimi.ingsw.Client.View.View;

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
        FIRST_ACCEPTED;
    }

    LoginNicknameRequest parent;
    Status nicknameStatus;



    public LoginNicknameAnswer(LoginNicknameRequest parent, Status nicknameStatus){
        this.parent = parent;
        this.nicknameStatus = nicknameStatus;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        LoginView loginView = (LoginView) serverHandler.getClient().getCurrentView();
        loginView.setLoginNicknameAnswer(this);
        serverHandler.getClient().getCurrentView().notifyView();
    }
}
