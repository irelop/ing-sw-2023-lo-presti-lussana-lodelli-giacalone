package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;

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
        serverHandler.sendMessageToClient(this);
    }
}
