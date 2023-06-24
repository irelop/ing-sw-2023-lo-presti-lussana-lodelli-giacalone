package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.view.CLI.GoalView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * This class creates a message with the score and information about the commonGoal and the personalGoal,
 * and send it to the client.
 *
 * @author Matteo Lussana
 */
public class GoalAndScoreMsg extends S2CMessage{

    public boolean commonGoalAchieved;
    public boolean personalGoalAchieved;
    public int score;
    public boolean youFullyShelf;
    public boolean lastTurn;

    public GoalAndScoreMsg(boolean commonGoalAchieved, boolean personalGoalAchieved, int score,
                           boolean youFullyShelf, boolean lastTurn){
        this.commonGoalAchieved = commonGoalAchieved;
        this.personalGoalAchieved = personalGoalAchieved;
        this.score = score;
        this.youFullyShelf = youFullyShelf;
        this.lastTurn = lastTurn;
    }

    @Override
    public void processMessage(ServerHandler serverHandler){
        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        } else {
            serverHandler.getOwner().transitionToView(new GoalView(this));
        }
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            } else {
                client.goToGoalView(this);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
