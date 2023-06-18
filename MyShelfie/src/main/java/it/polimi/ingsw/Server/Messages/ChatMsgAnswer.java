package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;

import it.polimi.ingsw.Client.View.GoalView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class ChatMsgAnswer extends S2CMessage{
    boolean isCorrect;

    public ChatMsgAnswer(boolean isCorrect){
        this.isCorrect = isCorrect;
    }
    @Override
    public void processMessage(ServerHandler serverHandler) {
        GoalView goalView = (GoalView) serverHandler.getOwner().getCurrentView();
        goalView.setSendingResult(isCorrect);
        goalView.notifyView();


    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        GoalView goalView = null;
        try {
            goalView = (GoalView) client.getOwner().getCurrentView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        goalView.setSendingResult(isCorrect);

    }
}
