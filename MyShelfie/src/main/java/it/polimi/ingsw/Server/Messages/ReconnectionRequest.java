package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class ReconnectionRequest extends C2SMessage{
    public String nickname;

    public ReconnectionRequest(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.getController().
                getGameRecord().getDisconnectedClientHandlerSocket(this.nickname, clientHandler);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            server.getController(client).getGameRecord().
                    getDisconnectedClientHandlerRMI(this.nickname, client);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
