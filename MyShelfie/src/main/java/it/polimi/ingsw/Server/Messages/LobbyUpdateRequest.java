package it.polimi.ingsw.Server.Messages;


import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * LobbyUpdateRequest: this message allows to properly the send the user request in order to get a snapshot of the actual
 * lobby players connected to the game.
 * @author Andrea Giacalone
 */
public class LobbyUpdateRequest extends C2SMessage{
    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.getController().updateLobby();
    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.getController(client).updateLobby();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
