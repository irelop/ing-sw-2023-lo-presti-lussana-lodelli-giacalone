package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.GameRecord;
import it.polimi.ingsw.Server.Model.MyShelfie;
import it.polimi.ingsw.Server.RMIClientHandler;
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
        clientHandler.setGameFromGameRecord();  //setting the controller
        clientHandler.getGameRecord().manageLogin(clientHandler,this,clientHandler.getController());
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.setMapClientsToController(client);
            RMIClientHandler clientHandler = new RMIClientHandler(client, server.getGameRecord());
            clientHandler.setGameFromGameRecord();  //setting the controller
            server.getGameRecord().manageLogin(clientHandler,this,server.getController(client));
            Thread thread = new Thread(clientHandler);
            thread.start();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
