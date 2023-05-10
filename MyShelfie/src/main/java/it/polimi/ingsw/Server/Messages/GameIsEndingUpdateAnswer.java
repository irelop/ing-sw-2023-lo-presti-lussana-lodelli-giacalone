package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.GameIsEndingView;
import it.polimi.ingsw.Client.View.LobbyView;

public class GameIsEndingUpdateAnswer extends S2CMessage{

    public boolean gameOver;
    public int playerIndex;

    public GameIsEndingUpdateAnswer(boolean gameOver, int playerIndex){
        this.gameOver = gameOver;
        this.playerIndex = playerIndex;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(new GameIsEndingView(this));
        serverHandler.getClient().getCurrentView().notifyView();
    }
}
