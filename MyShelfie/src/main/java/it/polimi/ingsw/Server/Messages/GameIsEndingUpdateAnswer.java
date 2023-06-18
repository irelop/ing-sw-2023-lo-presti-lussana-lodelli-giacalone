package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.GameIsEndingView;
import it.polimi.ingsw.Client.View.LobbyView;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.Model.Player;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameIsEndingUpdateAnswer extends S2CMessage{

    public boolean gameOver;
    public int playerIndex;
    public int firstToFinish;
    public String[] players;
    public boolean[] hasFinished;

    public GameIsEndingUpdateAnswer(boolean gameOver, int playerIndex, int firstToFinish,
                                    String[] players, boolean[] hasFinished){
        this.gameOver = gameOver;
        this.playerIndex = playerIndex;
        this.firstToFinish = firstToFinish;
        this.players = new String[players.length];
        this.hasFinished = new boolean[hasFinished.length];
        for(int i=0; i<players.length; i++){
            this.players[i] = players[i];
            this.hasFinished[i] = hasFinished[i];
        }
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().loadNextStage("waiting.fxml");
        } else {
            serverHandler.getOwner().transitionToView(new GameIsEndingView(this));
            serverHandler.getOwner().getCurrentView().notifyView();
        }
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().loadNextStage("waiting.fxml");
            } else {
                client.goToGameIsEndingView(this);
                if (client.getCurrentView().getClass() == WaitingView.class)
                    client.notifyView();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}