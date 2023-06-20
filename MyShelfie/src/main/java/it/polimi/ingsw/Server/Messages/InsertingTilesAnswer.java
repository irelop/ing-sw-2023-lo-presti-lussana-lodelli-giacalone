package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.view.CLI.InsertInShelfView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * This class represents a message sent from server to client.
 * It represents the answer given after checking if user's inputs
 * are right (no exception generated in the model).
 * Valid attribute is set to false if user didn't insert a valid input,
 * in order to ask again the same input
 *
 * @see InsertingTilesMsg
 *
 * @author Riccardo Lodelli
 */
public class InsertingTilesAnswer extends S2CMessage {
    public String answer;
    public boolean valid;

    public InsertingTilesAnswer(String answer, boolean valid){
        this.answer = answer;
        this.valid = valid;
    }


    @Override
    public void processMessage(ServerHandler serverHandler){
        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        } else {
        InsertInShelfView view = (InsertInShelfView) serverHandler.getOwner().getCurrentView();
        view.setInsertingTilesAnswer(this);
        serverHandler.getOwner().getCurrentView().notifyView();
        }
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try{
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            } else {
                InsertInShelfView view = (InsertInShelfView) client.getCurrentView();
                view.setInsertingTilesAnswer(this);
                //client.notifyView();
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

}
