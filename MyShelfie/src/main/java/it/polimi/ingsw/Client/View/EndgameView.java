package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.FinishTurnMsg;
import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;
import java.util.Collections;
import java.util.Scanner;

public class EndgameView extends View{
    private ScoreBoardMsg msg;

    public EndgameView(ScoreBoardMsg msg){
        this.msg = msg;
    }

    @Override
    public void run(){
        Integer goOn;
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
            System.out.println((i+1)+"° - "+msg.playerName.get(i).getNickname()+" with "+msg.totalScore.get(i));
        }
        System.out.println("---------------------------------");
        System.out.println("[press any key to continue]");
        Scanner scanner = new Scanner(System.in);
        goOn = scanner.nextInt();

        if(goOn != null){
            getOwner().transitionToView(nextView);
            FinishTurnMsg finishTurnMsg = new FinishTurnMsg();
        }
    }
}
