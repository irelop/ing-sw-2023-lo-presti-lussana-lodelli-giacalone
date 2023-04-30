package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LobbyView;

import java.util.ArrayList;

public class LobbyUpdateAnswer extends S2CMessage{
    private ArrayList<String> lobbyPlayers;

    public LobbyUpdateAnswer(ArrayList<String> lobbyPlayers) {
        this.lobbyPlayers = lobbyPlayers;
    }


    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(new LobbyView(lobbyPlayers));



    }
}
