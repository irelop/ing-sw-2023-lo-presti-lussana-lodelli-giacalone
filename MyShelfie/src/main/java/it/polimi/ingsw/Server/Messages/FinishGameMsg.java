package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Server.ClientHandler;

public class FinishGameMsg extends C2SMessage{
    //forse è inutile
    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.getController().finishGame();
    }
}
