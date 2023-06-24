package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import java.rmi.RemoteException;

/**
 * This message is sent by a player that has finished to see the scoreboard or which countdown is terminated
 * @author Irene Lo Presti, Andrea Giacalone
 */

public class FinishGameRequest extends C2SMessage{

    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.getController().finishGame(clientHandler);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
       try {
            server.getController(client).finishGameRMI(client);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
