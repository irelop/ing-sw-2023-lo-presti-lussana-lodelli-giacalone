package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.LoginView;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.RemoteInterface;
import java.rmi.RemoteException;

/**
 * This message is used only if there are problems with the player's connection:
 *      1) all players connect and set their nickname before the first player sets the number of players.
 *          If the first player sets a number smaller than the players connected this message is sent to
 *          the supernumerary players
 *      2) the first player connects, set their nickname and disconnects before setting the number of players.
 *          This message is sent to all the players connected to the game in order to connect them
 *          to another one.
 * @author Irene Lo Presti
 */
public class NumberOfPlayerManagementMsg extends S2CMessage{

    public String nickname;

    public NumberOfPlayerManagementMsg(String nickname){
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().loadNextStage("loginPlayer.fxml");
        } else {
            serverHandler.getOwner().transitionToView(new LoginView(this));
            serverHandler.getOwner().getCurrentView().notifyView();
        }
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().loadNextStage("loginPlayer.fxml");
            } else {
                client.goToLoginView(this);
                if (client.getCurrentView().getClass() == WaitingView.class)
                    client.notifyView();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
