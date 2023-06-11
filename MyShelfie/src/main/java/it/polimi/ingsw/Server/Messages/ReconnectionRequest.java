package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.GameRecord;
import it.polimi.ingsw.Server.RMIClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class ReconnectionRequest extends C2SMessage{
    public String nickname;

    public ReconnectionRequest(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.getGameRecord().reconnectPlayer(nickname, clientHandler);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            RMIClientHandler rmiClientHandler = new RMIClientHandler(client, server.getGameRecord());
            server.getGameRecord().reconnectPlayer(nickname, rmiClientHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
