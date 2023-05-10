package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.FinishGameMsg;
import it.polimi.ingsw.Server.Messages.FinishTurnMsg;
import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;
import java.util.Collections;
import java.util.Scanner;

public class EndgameView extends View{
    private final ScoreBoardMsg msg;

    public EndgameView(ScoreBoardMsg msg){
        this.msg = msg;
    }

    @Override
    public void run(){

        //sorting arrayList
        for(int i=0; i<msg.playerName.size(); i++)
            for(int j=0; j<msg.playerName.size()-1; j++){
                if(msg.totalScore.get(j)<msg.totalScore.get(j+1) && i!=j){
                    Collections.swap(msg.totalScore,j,j+1);
                    Collections.swap(msg.playerName,j,j+1);
                }
            }

        System.out.println("---------------------------------");
        System.out.println("SCORE BOARD:");
        for(int i=0; i<msg.playerName.size(); i++){
            System.out.println((i+1)+"° - "+msg.playerName.get(i)+" with "+msg.totalScore.get(i)+" pt.");
        }

        System.out.println("---------------------------------");
        System.out.println("[press enter to continue]");

        Scanner scanner = new Scanner(System.in);
        String goOn;
        goOn = scanner.next();

        if(goOn != null){
            FinishGameMsg finishGameMsg = new FinishGameMsg();
            getOwner().getServerHandler().sendMessageToServer(finishGameMsg);
            getOwner().setTrueTerminate();
        }
    }
}
