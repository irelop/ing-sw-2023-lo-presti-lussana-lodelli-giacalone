package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.GoalView;
import it.polimi.ingsw.Client.View.InsertInShelfView;

/**
 * This class creates a message with the score and information about the commonGoal and the personalGoal,
 * and send it to the client.
 *
 * @author Matteo Lussana
 */
public class GoalAndScoreMsg extends S2CMessage{

    public boolean commonGoalAchived;
    public boolean personalGoalAchived;
    public int score;
    public boolean youFullyShelf;
    public boolean lastTurn;

    public GoalAndScoreMsg(boolean commonGoalAchived, boolean personalGoalAchived, int score,
                           boolean youFullyShelf, boolean lastTurn){
        this.commonGoalAchived = commonGoalAchived;
        this.personalGoalAchived = personalGoalAchived;
        this.score = score;
        this.youFullyShelf = youFullyShelf;
        this.lastTurn = lastTurn;
    }

    @Override
    public void processMessage(ServerHandler serverHandler){
        serverHandler.getClient().transitionToView(new GoalView(this));
    }

}
