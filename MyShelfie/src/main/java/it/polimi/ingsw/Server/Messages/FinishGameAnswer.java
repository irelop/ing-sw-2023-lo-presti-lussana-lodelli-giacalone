package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.EndgameView;

public class FinishGameAnswer extends S2CMessage{
    String farewellMessage;

    public FinishGameAnswer(String farewellMessage){
        this.farewellMessage = farewellMessage;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        EndgameView endgameView = (EndgameView) serverHandler.getClient().getCurrentView();
        endgameView.setFarewellFromServer(farewellMessage);
        endgameView.notifyView();
    }
}
