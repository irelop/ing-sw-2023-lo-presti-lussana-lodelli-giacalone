package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LobbyView;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;
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
        serverHandler.getOwner().transitionToView(new LobbyView(this));
        serverHandler.getOwner().getCurrentView().notifyView();


    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            client.goToLobbyView(this);
            if(client.getCurrentView().getClass() == WaitingView.class)
                client.notifyView();

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
