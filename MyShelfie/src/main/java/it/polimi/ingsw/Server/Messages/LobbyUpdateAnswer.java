package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LobbyView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class LobbyUpdateAnswer extends S2CMessage{
    /*private*/public ArrayList<String> lobbyPlayers;
    public boolean allPlayersReady;
    public boolean lastRMIConnected;

    public LobbyUpdateAnswer(ArrayList<String> lobbyPlayers, boolean allPlayersReady) {
        this.lobbyPlayers = lobbyPlayers;
        this.allPlayersReady = allPlayersReady;
        this.lastRMIConnected = false;
    }

    public LobbyUpdateAnswer(ArrayList<String> lobbyPlayers, boolean allPlayersReady,boolean lastRMIConnected) {
        this.lobbyPlayers = lobbyPlayers;
        this.allPlayersReady = allPlayersReady;
        this.lastRMIConnected = lastRMIConnected;
    }



    @Override
    public void processMessage(ServerHandler serverHandler) {
        //serverHandler.getClient().transitionToView(new LobbyView(lobbyPlayers));
        serverHandler.getClient().transitionToView(new LobbyView(this));
        serverHandler.getClient().getCurrentView().notifyView();


    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            client.goToLobbyView(this);
            if(!lastRMIConnected) client.notifyView();

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
