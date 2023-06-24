package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;

import it.polimi.ingsw.Client.view.CLI.GoalView;
import it.polimi.ingsw.Server.chat.ChatStorage;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * ChatRecordAnswer: this message wraps the snapshot of the actual record of chat messages according to their
 * privacy policy.
 * @author Andrea Giacalone
 */
public class ChatRecordAnswer extends S2CMessage{
    private final ChatStorage chatStorage;

    public ChatRecordAnswer(ChatStorage chatStorage){
        this.chatStorage= chatStorage;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        if(serverHandler.getOwner().gui){
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        }
        else{
            GoalView goalView = (GoalView) serverHandler.getOwner().getCurrentView();
            goalView.setChat(chatStorage);
            serverHandler.getOwner().getCurrentView().notifyView();
        }


    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        GoalView goalView;
        try {
            if(client.getOwner().gui){
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            }
            else{
                goalView = (GoalView) client.getOwner().getCurrentView();
                goalView.setChat(chatStorage);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }



    }

    public ChatStorage getChatStorage() {
        return chatStorage;
    }
}
