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
        S2CMessage loginNicknameAnswer;



        if(controller.checkNickname(insertedNickname) == true){
            controller.addPlayer(insertedNickname);
            if(controller.isFirstConnected(insertedNickname) == true ){
                loginNicknameAnswer = new LoginNicknameAnswer(this, LoginNicknameAnswer.Status.FIRST_ACCEPTED);


            }else{
                loginNicknameAnswer = new LoginNicknameAnswer(this, LoginNicknameAnswer.Status.ACCEPTED);
            }


        }else{
            loginNicknameAnswer = new LoginNicknameAnswer(this, LoginNicknameAnswer.Status.INVALID);
        }

        clientHandler.sendMessageToClient(loginNicknameAnswer);
    }
}
