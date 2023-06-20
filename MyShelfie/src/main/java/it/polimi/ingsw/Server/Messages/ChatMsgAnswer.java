package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;

import it.polimi.ingsw.Client.view.CLI.GoalView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

public class ChatMsgAnswer extends S2CMessage{
    boolean isCorrect;

    public ChatMsgAnswer(boolean isCorrect){
        this.isCorrect = isCorrect;
    }
    @Override
    public void processMessage(ServerHandler serverHandler) {
        if(serverHandler.getOwner().gui){
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        }
        else{
            GoalView goalView = (GoalView) serverHandler.getOwner().getCurrentView();
            goalView.setSendingResult(isCorrect);
            goalView.notifyView();
        }


    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        GoalView goalView = null;
        try {
            if(client.getOwner().gui){
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            }
            else{
                goalView = (GoalView) client.getOwner().getCurrentView();
                goalView.setSendingResult(isCorrect);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }


    }
}
