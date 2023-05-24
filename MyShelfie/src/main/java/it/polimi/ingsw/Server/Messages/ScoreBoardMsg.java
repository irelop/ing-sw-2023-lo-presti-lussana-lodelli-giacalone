package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.EndgameView;
import it.polimi.ingsw.Client.View.InsertInShelfView;
import it.polimi.ingsw.Server.Model.Player;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ScoreBoardMsg extends S2CMessage{
    public ArrayList<String> playerName;
    public ArrayList<Integer> totalScore;

    public ScoreBoardMsg(ArrayList<String> playerName, ArrayList<Integer> totalScore){
        this.playerName = playerName;
        this.totalScore = totalScore;
    }

    @Override
    public void processMessage(ServerHandler serverHandler){
        serverHandler.getClient().transitionToView(new EndgameView(this));
        serverHandler.getClient().getCurrentView().notifyView();
    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            client.goToEndgameView(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
