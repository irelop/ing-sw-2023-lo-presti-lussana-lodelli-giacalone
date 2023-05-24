package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

public class GameIsEndingUpdateRequest extends C2SMessage{
//serve????????????????
    //non credo
    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.getController().updateGameIsEndingView();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){}
}
