package it.polimi.ingsw.Client.Gui;

import it.polimi.ingsw.Server.Messages.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import static java.lang.Thread.sleep;

/**
 * Controller implementation for the login management
 * It contains a form to insert player's nickname and some other stuff
 * @see Controller
 */
public class ReconnectionController extends Controller{

    private ReconnectionAnswer answer;

    @FXML
    private TextField playerNickname;
    @FXML
    private Text reconnectionResult;
    @FXML
    private Button backButton;


    /**
     * This JavaFX method allows to make some operations when the window is created
     * In this case it set some default properties for some objects
     */
    @FXML
    public void initialize() {

    }

    @Override
    public void build(S2CMessage message) {
    }

    @Override
    public void receiveAnswer(S2CMessage message) {
        answer = (ReconnectionAnswer) message;
        manageReconnection();
    }

    /**
     * This method is called when the Submit button is clicked
     * It sends some messages to the server to register a new player
     * or to set the numberOfPlayers for the next game
     */
    @FXML
    private void submitNicknameRequest() {

        String playerName = playerNickname.getText().toUpperCase();
        if (playerName.isEmpty()) {
            // case: text field empty
            reconnectionResult.setText("PLEASE TYPE SOMETHING");
        }else{
            getOwner().setNickname(playerName);
            ReconnectionRequest reconnectionRequest = new ReconnectionRequest(playerName, true);
            getOwner().getServerHandler().sendMessageToServer(reconnectionRequest);
        }
    }

    /**
     * This method is called when the GoBack button is clicked
     * It returns to the titleScreen window
     */
    @FXML
    private void returnBack() {
        getOwner().getStageManager().loadNextStage("titleScreen.fxml");
    }

    /**
     * With this method user can press ENTER instead of
     * clicking the Submit button
     * @param e: event containing the key pressed
     */
    public void manageKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER) submitNicknameRequest();
    }


    public void manageReconnection(){
        if(answer.msg != null) {
            reconnectionResult.setText(answer.msg);
            Thread delayingComputationThread = new Thread(this::delayComputation);
            delayingComputationThread.start();
        }
        else{
            getOwner().getStageManager().loadNextStage("waiting.fxml");
        }
    }

    private void delayComputation(){
        for(int i = 0; i<5; i++){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        getOwner().getStageManager().loadNextStage("loginPlayer.fxml");
    }
}
