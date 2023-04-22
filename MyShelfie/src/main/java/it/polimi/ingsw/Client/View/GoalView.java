package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.GoalAndScoreMsg;

public class GoalView extends View{

    private GoalAndScoreMsg msg;

    public GoalView(GoalAndScoreMsg msg){
        this.msg = msg;
    }

    @Override
    public void run(){
        System.out.println("---------------------------------");
        System.out.println("GOAL ACHIVED IN THIS TURN:");
        if(msg.commonGoalAchived == true){
            System.out.println("Common Goal: yes");
        }else System.out.println("Common Goal: no");
        if(msg.personalGoalAchived == true){
            System.out.println("Personal Goal: yes");
        }else System.out.println("Personal Goal: no");

        System.out.println("Total score: "+ msg.score);
        System.out.println("---------------------------------");

    }





}
