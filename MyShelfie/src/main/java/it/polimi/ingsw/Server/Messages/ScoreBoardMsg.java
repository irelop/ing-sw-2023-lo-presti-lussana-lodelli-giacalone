package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.view.CLI.EndGameView;
import it.polimi.ingsw.Client.view.CLI.WaitingView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
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
            serverHandler.getOwner().getStageManager().loadNextStage(this, "endGame.fxml");
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
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
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            } else {
                client.goToEndgameView(this);
                if (client.getCurrentView().getClass() == WaitingView.class)
                    client.notifyView();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
