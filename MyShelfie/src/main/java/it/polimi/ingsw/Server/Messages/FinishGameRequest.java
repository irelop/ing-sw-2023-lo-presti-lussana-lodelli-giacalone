package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;

public class FinishGameRequest extends C2SMessage{
    //forse è inutile
    @Override
    public void processMessage(ClientHandler clientHandler){

        clientHandler.getController().finishGame(clientHandler);
    }
}
