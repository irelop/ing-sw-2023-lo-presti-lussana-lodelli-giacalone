package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import java.rmi.RemoteException;

/**
 * This message is sent by a player that has finished his/her turn,
 * so the next player can continue the game
 * @author Matteo Lussana
 */
public class FinishTurnMsg extends C2SMessage{

    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.getController().setNextPlayer();
    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.getController(client).setNextPlayer();

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
