package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

public class ReconnectionRequest extends C2SMessage{
    public String nickname;

    public ReconnectionRequest(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ClientHandler clientHandler) {
        ClientHandler reconnectedClientHandler = clientHandler.getController().getGameRecord().getDisconnectedClientHandler(this.nickname);

    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {

    }
}
