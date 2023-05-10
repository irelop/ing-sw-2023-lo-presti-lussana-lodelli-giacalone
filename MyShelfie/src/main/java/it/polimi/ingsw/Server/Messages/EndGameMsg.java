package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.ClientHandler;

public class EndGameMsg extends C2SMessage{

    private int playerIndex;

    public EndGameMsg(int playerIndex){
        this.playerIndex = playerIndex;
    }

    @Override
    public void processMessage(ClientHandler clientHandler){
            clientHandler.getController().endGame(playerIndex);

    }
}
