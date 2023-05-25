package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.EndgameView;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

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
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        EndgameView endgameView = null;
        try {
            endgameView = (EndgameView) client.getCurrentView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        endgameView.setFarewellFromServer(farewellMessage);
    }
}
