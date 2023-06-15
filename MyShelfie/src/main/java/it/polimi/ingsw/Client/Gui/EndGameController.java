package it.polimi.ingsw.Client.Gui;

/**
 * Controller that manages the last GUI view that player will see
 * it shows the scoreboard
 * @author Matteo Lussana
 * @see Controller
 */

import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.util.Collections;


public class EndGameController extends Controller {

    @FXML Text textPos_1;
    @FXML Text textPos_2;
    @FXML Text textPos_3;
    @FXML Text textPos_4;

    @FXML Text textPos_1_score;
    @FXML Text textPos_2_score;
    @FXML Text textPos_3_score;
    @FXML Text textPos_4_score;

    @FXML ImageView podFirst;
    @FXML ImageView podSecond;
    @FXML ImageView podThird;

    /**
     * This method sets all the elements from message parameters,
     * it orders the array of players based on their scores and
     * puts players data in the correct spot
     * @param message: ToShelfMsg
     */
    @Override
    public void build(S2CMessage message) {
        podThird.setVisible(false);
        ScoreBoardMsg msg = (ScoreBoardMsg) message;

        //sorting arrayList
        for (int i = 0; i < msg.playerName.size(); i++)
            for (int j = 0; j < msg.playerName.size() - 1; j++) {
                if (msg.totalScore.get(j) < msg.totalScore.get(j + 1) && i != j) {
                    Collections.swap(msg.totalScore, j, j + 1);
                    Collections.swap(msg.playerName, j, j + 1);
                }
            }

        textPos_1.setText(msg.playerName.get(0));
        textPos_1.setVisible(true);
        textPos_1_score.setText(msg.totalScore.get(0).toString()+" pt.");
        textPos_1_score.setVisible(true);

        textPos_2.setText( msg.playerName.get(1));
        textPos_2.setVisible(true);
        textPos_2_score.setText(msg.totalScore.get(1).toString()+" pt.");
        textPos_2_score.setVisible(true);
        if (msg.playerName.size() > 2) {
            textPos_3.setText(msg.playerName.get(2));
            textPos_3.setVisible(true);
            textPos_3_score.setText(msg.totalScore.get(2).toString()+" pt.");
            textPos_3_score.setVisible(true);
            podThird.setVisible(true);
            if (msg.playerName.size() > 3) {
                textPos_4.setText(msg.playerName.get(3));
                textPos_4.setVisible(true);
                textPos_4_score.setText(msg.totalScore.get(3).toString()+" pt.");
                textPos_4_score.setVisible(true);
            }
        }
        //resize();

    }

    @Override
    public void receiveAnswer(S2CMessage message) {

    }
    /*
    public void resize(){
        Group names = new Group();
        names.getChildren().add(textPos_1);
        names.getChildren().add(textPos_2);
        names.getChildren().add(textPos_3);
        names.getChildren().add(textPos_4);

        for(Node node : names.getChildren()){
            if(node!=null && ((Text) node).getText().length()>8) ((Text) node).setFont(Font.font(14));
        }
    }
    */
}
