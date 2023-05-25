package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.GameIsEndingView;
import it.polimi.ingsw.Client.View.LobbyView;
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
    public boolean isRMIFirstLobby;

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

        this.isRMIFirstLobby = false;
    }



    public GameIsEndingUpdateAnswer(boolean gameOver, int playerIndex, int firstToFinish,
                                    String[] players, boolean[] hasFinished,boolean isRMIFirstLobby){
        this.gameOver = gameOver;
        this.playerIndex = playerIndex;
        this.firstToFinish = firstToFinish;
        this.players = new String[players.length];
        this.hasFinished = new boolean[hasFinished.length];
        for(int i=0; i<players.length; i++){
            this.players[i] = players[i];
            this.hasFinished[i] = hasFinished[i];
        }

        this.isRMIFirstLobby = isRMIFirstLobby;
    }




    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(new GameIsEndingView(this));
        serverHandler.getClient().getCurrentView().notifyView();
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            System.out.println("process");
            client.goToGameIsEndingView(this);

            if(!isRMIFirstLobby) {
                System.out.println("chiamo notify");
                client.notifyView();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}