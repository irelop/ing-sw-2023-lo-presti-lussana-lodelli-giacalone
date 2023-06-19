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

        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().loadNextStage(this,"lobby.fxml");
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        } else {
            serverHandler.getOwner().transitionToView(new LobbyView(this));
            serverHandler.getOwner().getCurrentView().notifyView();
        }

    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().loadNextStage(this,"lobby.fxml");
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            }else{
                client.goToLobbyView(this);
                if(client.getCurrentView().getClass() == WaitingView.class)
                    client.notifyView();
            }


        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
