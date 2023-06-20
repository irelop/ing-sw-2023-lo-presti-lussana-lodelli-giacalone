package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

public class LoginNumPlayersRequest extends C2SMessage{
    private int insertedNumPlayers;

    public LoginNumPlayersRequest(int insertedNumPlayers) {
        this.insertedNumPlayers = insertedNumPlayers;
    }
    @Override
    public void processMessage(ClientHandler clientHandler) {

        clientHandler.getController().setNumberOfPlayers(insertedNumPlayers);

    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.getController(client).setNumberOfPlayers(insertedNumPlayers);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
