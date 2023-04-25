package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;

import java.util.concurrent.TimeUnit;


public class LoginNicknameRequest extends C2SMessage{

    String insertedNickname;

    public LoginNicknameRequest(String insertedNickname){ this.insertedNickname = insertedNickname; }

    @Override
    public void processMessage(ClientHandler clientHandler) {

    }

    public String getInsertedNickname() {
        return insertedNickname;
    }

    /*@Override
    public void processMessage(ClientHandler clientHandler) {

        clientHandler.getController().checkNickname(insertedNickname);
        S2CMessage loginNicknameAnswer;

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ex) {
        }
        if(clientHandler.getController().checkNickname() == true){
            if(clientHandler.getController().isFirstConnected(insertedNickname) == true ){
                loginNicknameAnswer = new LoginNicknameAnswer(this, LoginNicknameAnswer.Status.FIRST_ACCEPTED);
            }else{
                loginNicknameAnswer = new LoginNicknameAnswer(this, LoginNicknameAnswer.Status.ACCEPTED);
            }
        }else{
            loginNicknameAnswer = new LoginNicknameAnswer(this, LoginNicknameAnswer.Status.INVALID);
        }

        clientHandler.sendMessageToClient(loginNicknameAnswer);
    }*/
}
