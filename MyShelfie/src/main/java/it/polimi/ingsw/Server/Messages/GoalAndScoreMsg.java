package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.View.GoalView;

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
    public processMessage(ServerHandler serverHandler)
    {
        serverHandler.getClient().transitionToView(new GoalView(this));
    }

}
