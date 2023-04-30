package it.polimi.ingsw.Server.Messages;


import it.polimi.ingsw.Server.ClientHandler;

public class LobbyUpdateRequest extends C2SMessage{

    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.getController().updateLobby();
    }
}
