package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.MyShelfie;

import java.util.concurrent.TimeUnit;


public class LoginNicknameRequest extends C2SMessage{

    String insertedNickname;

    public LoginNicknameRequest(String insertedNickname){ this.insertedNickname = insertedNickname; }


    public String getInsertedNickname() {
        return insertedNickname;
    }

    @Override
    public void processMessage(ClientHandler clientHandler) {

        MyShelfie controller = clientHandler.getController();
        controller.manageLogin(clientHandler,this);
    }
}
