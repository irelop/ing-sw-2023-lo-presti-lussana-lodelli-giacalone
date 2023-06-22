package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RMIClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * ReconnectionRequest: this method allows to properly send to the server the user reconnection request.
 * @authors Irene Lo Presti, Andrea Giacalone
 */
public class ReconnectionRequest extends C2SMessage{
    public String nickname;
    boolean isGui;

    public ReconnectionRequest(String insertedNickname, boolean isGui){
        this.nickname = insertedNickname;
        this.isGui = isGui;
    }

    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.getGameRecord().reconnectPlayer(nickname, clientHandler, isGui);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            RMIClientHandler reconnectedHandler = new RMIClientHandler(client, server.getGameRecord());
            server.getGameRecord().reconnectPlayer(nickname, reconnectedHandler, isGui);

            //need to create a new Thread in order to handle connection in RMI
            Thread reconnectedHandlerThread = new Thread(reconnectedHandler);
            reconnectedHandlerThread.start();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
