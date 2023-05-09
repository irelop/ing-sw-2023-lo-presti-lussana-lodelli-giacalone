package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LobbyView;

import java.util.ArrayList;

public class LobbyUpdateAnswer extends S2CMessage{
    /*private*/public ArrayList<String> lobbyPlayers;
    public boolean allPlayersReady;

    public LobbyUpdateAnswer(ArrayList<String> lobbyPlayers, boolean allPlayersReady) {
        this.lobbyPlayers = lobbyPlayers;
        this.allPlayersReady = allPlayersReady;
    }


    @Override
    public void processMessage(ServerHandler serverHandler) {
        //serverHandler.getClient().transitionToView(new LobbyView(lobbyPlayers));
        serverHandler.getClient().transitionToView(new LobbyView(this));
        serverHandler.getClient().getCurrentView().notifyView();


    }
}
