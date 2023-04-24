package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.AskLobbySizeView;
import it.polimi.ingsw.Client.View.WaitingLobbyView;

public class LoginNicknameAnswer extends S2CMessage{
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
        if(this.nicknameStatus == Status.FIRST_ACCEPTED) {
            serverHandler.getClient().transitionToView(new AskLobbySizeView());
        }else{
            serverHandler.getClient().transitionToView(new WaitingLobbyView());
        }
    }
}
