package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import java.rmi.RemoteException;

/**
 * This message starts the game when all players are connected and ready
 *          (they set the nickname and the number of players)
 * @author Irene Lo Presti
 */

public class AllPlayersReadyMsg extends C2SMessage{

    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.getController().initGame();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.getController(client).initGame();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
