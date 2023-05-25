package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class EndGameMsg extends C2SMessage{

    private int playerIndex;

    public EndGameMsg(int playerIndex){
        this.playerIndex = playerIndex;
    }

    @Override
    public void processMessage(ClientHandler clientHandler){
            clientHandler.getController().endGame(playerIndex);

    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.getController(client).endGame(playerIndex);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
