package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.EndGameView;
import it.polimi.ingsw.Server.RemoteInterface;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ScoreBoardMsg extends S2CMessage{
    public ArrayList<String> playerName;
    public ArrayList<Integer> totalScore;
    public String playerNickname;

    public ScoreBoardMsg(ArrayList<String> playerName, ArrayList<Integer> totalScore, String playerNickname){
        this.playerName = playerName;
        this.totalScore = totalScore;
        this.playerNickname = playerNickname;
    }

    @Override
    public void processMessage(ServerHandler serverHandler){
        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().loadNextStage(this,"endGame.fxml");
        } else {
            serverHandler.getOwner().transitionToView(new EndGameView(this));
            serverHandler.getOwner().getCurrentView().notifyView();
        }
    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().loadNextStage(this,"endGame.fxml");
            } else {
                client.goToEndgameView(this);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
