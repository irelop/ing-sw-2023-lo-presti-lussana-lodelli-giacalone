package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.view.CLI.ChooseTilesFromBoardView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * This message is send by the server to the client, it contains the answer whether the initial position
 * is valid or not
 *
 * @author Irene Lo Presti
 */

public class InitialPositionAnswer extends S2CMessage{

    public String answer;
    public boolean valid;

    public InitialPositionAnswer(String answer, boolean valid){
        this.answer = answer;
        this.valid = valid;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        } else {
            ChooseTilesFromBoardView chooseTilesFromBoardView = (ChooseTilesFromBoardView) serverHandler.getOwner().getCurrentView();
            chooseTilesFromBoardView.setInitialPositionAnswer(this);
            serverHandler.getOwner().getCurrentView().notifyView();
        }
    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try{
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            } else {
            ChooseTilesFromBoardView chooseTilesFromBoardView = (ChooseTilesFromBoardView) client.getCurrentView();
            chooseTilesFromBoardView.setInitialPositionAnswer(this);
            }
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

}
