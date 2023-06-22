package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RMIClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * LoginNicknameRequest: this message allows to properly send to the server the user request regarding its nickname in
 * order to be checked for its validity.
 * @author Andrea Giacalone
 */
public class LoginNicknameRequest extends C2SMessage{

    String insertedNickname;
    boolean isGui;

    public LoginNicknameRequest(String insertedNickname, boolean isGui){
        this.insertedNickname = insertedNickname;
        this.isGui = isGui;
    }


    public String getInsertedNickname() {
        return insertedNickname;
    }

    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.setGameFromGameRecord();  //setting the controller
        clientHandler.getGameRecord().manageLogin(clientHandler,this,clientHandler.getController(),isGui);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.setControllerToClient(client);
            RMIClientHandler clientHandler = new RMIClientHandler(client, server.getGameRecord());
            clientHandler.setGameFromGameRecord();  //setting the controller
            server.getGameRecord().manageLogin(clientHandler,this,server.getController(client),isGui);
            Thread thread = new Thread(clientHandler);
            thread.start();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
