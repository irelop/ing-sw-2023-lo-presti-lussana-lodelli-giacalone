package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;
import java.util.Collections;

public class EndgameView extends View{
    private ScoreBoardMsg msg;

    public EndgameView(ScoreBoardMsg msg){
        this.msg = msg;
    }

    @Override
    public void run(){
        View nextView = null;

        //sorting arrayList
        for(int i=0; i<msg.playerName.size(); i++)
            for(int j=1; j<msg.playerName.size()-1; j++){
                if(msg.totalScore.get(i)<msg.totalScore.get(j)){
                    Collections.swap(msg.totalScore,i,j);
                    Collections.swap(msg.playerName,i,j);
                }
            }

        System.out.println("---------------------------------");
        System.out.println("SCORE BOARD:");
        for(int i=0; i<msg.playerName.size(); i++){
            System.out.println((i+1)+"° - "+msg.playerName+" with "+msg.totalScore);
        }
        System.out.println("---------------------------------");
    }


}
