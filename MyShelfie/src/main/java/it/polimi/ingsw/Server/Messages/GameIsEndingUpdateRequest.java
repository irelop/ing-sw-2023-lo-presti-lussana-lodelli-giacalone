package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;

public class GameIsEndingUpdateRequest extends C2SMessage{
//serve????????????????
    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.getController().updateGameIsEndingView();
    }
}
