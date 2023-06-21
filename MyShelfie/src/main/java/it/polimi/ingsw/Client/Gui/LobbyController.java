package it.polimi.ingsw.Client.Gui;

import it.polimi.ingsw.Server.Messages.AllPlayersReadyMsg;
import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.LobbyUpdateAnswer;
import it.polimi.ingsw.Server.Messages.S2CMessage;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.concurrent.TimeUnit;

/**
 * Controller implementation for game's lobby
 * It contains a pane foreach player already connected
 * @see Controller
 */
public class LobbyController extends Controller{

    private LobbyUpdateAnswer lobbyUpdateAnswer;
    @FXML
    private FlowPane playersFlowPane;

    /**
     * Shows a pane foreach player connected to the game
     * @param msg: the server message containing values
     */
    @Override
    public void build(S2CMessage msg){

        this.lobbyUpdateAnswer = (LobbyUpdateAnswer) msg;
        int i = 0;
        for (Node player:
                playersFlowPane.getChildren()) {
            Pane playerPane = (Pane) player;
            if (i < lobbyUpdateAnswer.lobbyPlayers.size()) {
                ((Label) playerPane.getChildren().get(0)).setText(lobbyUpdateAnswer.lobbyPlayers.get(i));
                playerPane.setVisible(true);
                i++;
            }
        }

    }

    /**
     * If the number of players has been reached starts the game after 5 seconds
     */
    @Override
    public void receiveAnswer(S2CMessage message) {
        if (lobbyUpdateAnswer.allPlayersReady) {
            /*
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ignored) { }
            */

            C2SMessage allPlayersReadyMsg = new AllPlayersReadyMsg();
            getOwner().getServerHandler().sendMessageToServer(allPlayersReadyMsg);
        }
    }

}
