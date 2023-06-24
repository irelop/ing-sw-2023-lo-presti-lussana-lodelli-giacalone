package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import java.rmi.RemoteException;

/**
 * This message calls the endGame() method in the controller after all players have played their turn
 * @author Irene Lo Presti
 */

public class EndGameMsg extends C2SMessage{

    private final int playerIndex;

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
