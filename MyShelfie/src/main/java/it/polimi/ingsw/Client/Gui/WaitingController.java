package it.polimi.ingsw.Client.Gui;

/**
 * GUI view which is shown between turns, while other player are playeing
 * @author Matteo Lussana
 * @see Controller
 */

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.Messages.EndGameMsg;
import it.polimi.ingsw.Server.Messages.GameIsEndingUpdateAnswer;
import it.polimi.ingsw.Server.Messages.S2CMessage;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.Random;

public class WaitingController extends Controller {
    @FXML GridPane grid1;
    @FXML GridPane grid2;
    @FXML
    Text currentPlayerText;



    @Override
    public void build(S2CMessage message) {
        if (message instanceof GameIsEndingUpdateAnswer)
            manageGameIsEnding( (GameIsEndingUpdateAnswer) message );
    }

    @Override
    public void receiveAnswer(S2CMessage message) {
        if (message instanceof GameIsEndingUpdateAnswer)
            manageGameIsEnding( (GameIsEndingUpdateAnswer) message );
    }

    private void manageGameIsEnding(GameIsEndingUpdateAnswer message) {
        currentPlayerText.setText(message.players[message.playerIndex] + " is playing...");
        if(message.gameOver){
            //playerIndex needed to choose the right clientHandler
            System.out.println("Everyone has played their turn!");
            C2SMessage endGameMsg = new EndGameMsg(message.playerIndex);
            getOwner().getServerHandler().sendMessageToServer(endGameMsg);
        }
    }

    /**
     * This method manage the click of a button in order to change the order of tiles in side gridPane
     */
    public void AnimationGrid(){
        String[] names = {"BLUE", "GREEN", "WHITE", "PINK", "YELLOW", "LIGHTBLUE"};

        Random random = new Random();

        int num = random.nextInt(5);;
        for(Node node : grid1.getChildren()){
            node.setId(names[num]);
            num--;
            if(num<0) num = 5;
        }
        num = random.nextInt(5);
        for(Node node : grid2.getChildren()){
            node.setId(names[num]);
            num--;
            if(num<0) num = 5;
        }
    }


}

