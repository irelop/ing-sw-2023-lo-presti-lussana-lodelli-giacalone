package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.GoalView;
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

    public GoalAndScoreMsg(boolean commonGoalAchived, boolean personalGoalAchived, int score){
        this.commonGoalAchived = commonGoalAchived;
        this.personalGoalAchived = personalGoalAchived;
        this.score = score;
    }

    @Override
    public void processMessage(ServerHandler serverHandler){
        //serverHandler.sendMessageToClient(this);
    }

}
