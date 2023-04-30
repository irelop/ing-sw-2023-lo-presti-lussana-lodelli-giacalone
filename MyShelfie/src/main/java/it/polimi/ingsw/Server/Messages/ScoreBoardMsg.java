package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.EndgameView;
import it.polimi.ingsw.Client.View.InsertInShelfView;
import it.polimi.ingsw.Server.Model.Player;

import java.util.ArrayList;

public class ScoreBoardMsg extends S2CMessage{
    public ArrayList<Player> playerName;
    public ArrayList<Integer> totalScore;

    public ScoreBoardMsg(ArrayList<Player> playerName, ArrayList<Integer> totalScore){
        this.playerName = playerName;
        this.totalScore = totalScore;
    }

    @Override
    public void processMessage(ServerHandler serverHandler){
        serverHandler.getClient().transitionToView(new EndgameView(this));
    }
}
