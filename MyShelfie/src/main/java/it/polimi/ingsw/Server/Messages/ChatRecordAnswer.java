package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;

import it.polimi.ingsw.Client.View.GoalView;
import it.polimi.ingsw.Server.Model.ChatStorage;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class ChatRecordAnswer extends S2CMessage{
    private ChatStorage chatStorage;

    public ChatRecordAnswer(ChatStorage chatStorage){
        this.chatStorage= chatStorage;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        GoalView goalView = (GoalView) serverHandler.getOwner().getCurrentView();
        goalView.setChat(chatStorage);
        serverHandler.getOwner().getCurrentView().notifyView();


    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        GoalView goalView = null;
        try {
            goalView = (GoalView) client.getClient().getCurrentView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        goalView.setChat(chatStorage);


    }
}
