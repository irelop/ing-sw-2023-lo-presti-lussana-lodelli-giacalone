package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.FinishTurnMsg;
import it.polimi.ingsw.Server.Messages.GameIsEndingUpdateRequest;
import it.polimi.ingsw.Server.Messages.GoalAndScoreMsg;
import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;

import java.rmi.RemoteException;
import java.util.Scanner;

public class GoalView extends View {

    private GoalAndScoreMsg msg;

    public GoalView(GoalAndScoreMsg msg) {
        this.msg = msg;
    }

    @Override
    public void run() {

        View nextView = null;
        String goOn;

        Scanner scanner = new Scanner(System.in);

        System.out.println("---------------------------------");
        System.out.println("GOAL ACHIVED IN THIS TURN:");
        if (msg.commonGoalAchived == true) {
            System.out.println("Common Goal: yes");
        } else System.out.println("Common Goal: no");
        if (msg.personalGoalAchived == true) {
            System.out.println("Personal Goal: yes");
        } else System.out.println("Personal Goal: no");
        if (msg.youFullyShelf) System.out.println("You earned 1 pt. for be the first to complete the shelf, SIUM");

        System.out.println("Total score: " + msg.score);
        System.out.println("---------------------------------");
        System.out.println("[press enter to continue]");
        goOn = scanner.nextLine();

        //non l'abbiamo usata questa cosa....
        //forse va bene così BOH
        if (msg.lastTurn) {
            GameIsEndingUpdateRequest gameIsEndingUpdateRequest = new GameIsEndingUpdateRequest();
        }

        if (goOn != null) {
            FinishTurnMsg finishTurnMsg = new FinishTurnMsg();
            if (!getOwner().isRMI()) {
                getOwner().getServerHandler().sendMessageToServer(finishTurnMsg);
                //getOwner().transitionToView(nextView);
            } else {
                try {
                    getOwner().getRemoteServer().sendMessageToServer(finishTurnMsg);
                    /*if(getOwner().getCurrentView() == this)
                        getOwner().getClient().transitionToView(null);*/
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}