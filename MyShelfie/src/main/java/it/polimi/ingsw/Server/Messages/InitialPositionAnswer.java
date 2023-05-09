package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ChooseTilesFromBoardView;

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
        System.out.println("sono nel costruttore");
        this.answer = answer;
        this.valid = valid;
        System.out.println("ho finito di costruire");
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        System.out.println("sono nella process");
        ChooseTilesFromBoardView chooseTilesFromBoardView = (ChooseTilesFromBoardView) serverHandler.getClient().getCurrentView();
        System.out.println("ho preso la current view");
        chooseTilesFromBoardView.setInitialPositionAnswer(this);
        serverHandler.getClient().getCurrentView().notifyView();
    }

}
