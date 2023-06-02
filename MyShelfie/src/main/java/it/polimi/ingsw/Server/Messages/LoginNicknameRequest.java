package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.MyShelfie;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;


public class LoginNicknameRequest extends C2SMessage{

    String insertedNickname;

    public LoginNicknameRequest(String insertedNickname){ this.insertedNickname = insertedNickname; }


    public String getInsertedNickname() {
        return insertedNickname;
    }

    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.setGameFromGameRecord();
        clientHandler.getController().manageLogin(clientHandler,this);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.getController(client).manageLoginRMI(this, client);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
