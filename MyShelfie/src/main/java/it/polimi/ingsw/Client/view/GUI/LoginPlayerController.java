package it.polimi.ingsw.Client.view.GUI;

import it.polimi.ingsw.Server.Messages.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import static java.lang.Thread.sleep;

/**
 * Controller implementation for the login management
 * It contains a form to insert player's nickname and some other stuff
 * @see Controller
 */
public class LoginPlayerController extends Controller{

    private LoginNicknameAnswer answer;
    private NumberOfPlayerManagementMsg answerPlayerManagement;
    volatile boolean answerReceived;

    @FXML
    private TextField playerNickname;
    @FXML
    private Text loginResult;
    @FXML
    private Button backButton;
    @FXML
    private Button submitButton;
    @FXML
    private ComboBox<String> numOfPlayersSelection;
    @FXML
    private Text numOfPlayersText;
    @FXML
    private Pane textPane;
    String playerName = null;

    /**
     * This JavaFX method allows to make some operations when the window is created
     * In this case it set some default properties for some objects
     */
    @FXML
    public void initialize() {

        ObservableList<String> items = FXCollections.observableArrayList("2", "3", "4");
        numOfPlayersSelection.setItems(items);
        numOfPlayersSelection.setOpacity(0);
        numOfPlayersSelection.setDisable(true);
        numOfPlayersText.setOpacity(0);

        textPane.setVisible(false);

        answerReceived = false;
    }

    @Override
    public void build(S2CMessage message) {
    }

    @Override
    public void receiveAnswer(S2CMessage message) {
        if(message instanceof LoginNicknameAnswer){
            answer = (LoginNicknameAnswer) message;
            manageNicknameAnswer();
        }
        if(message instanceof NumberOfPlayerManagementMsg){
            answerPlayerManagement = (NumberOfPlayerManagementMsg) message;
            manageKickedOut();
        }
    }

    /**
     * This method is called when the Submit button is clicked
     * It sends some messages to the server to register a new player
     * or to set the numberOfPlayers for the next game
     */
    @FXML
    private void submitNicknameRequest() {
        playerName = playerNickname.getText().toUpperCase();
        if (playerName.isEmpty()) {
            // case: text field empty
            loginResult.setText("PLEASE TYPE SOMETHING");
        } else if (numOfPlayersSelection.getOpacity() == 1) {
            // case: first player connected has to insert number of players
            C2SMessage numPlayerRequest = new LoginNumPlayersRequest(Integer.parseInt(numOfPlayersSelection.getValue()));
            getOwner().getServerHandler().sendMessageToServer(numPlayerRequest);
            // sending the user to the lobby
            C2SMessage lobbyUpdate = new LobbyUpdateRequest();
            getOwner().getServerHandler().sendMessageToServer(lobbyUpdate);
        } else {
            // other cases
            C2SMessage nicknameRequest = new LoginNicknameRequest(playerName, true);
            getOwner().getServerHandler().sendMessageToServer(nicknameRequest);
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
     * This method is called when the client receives a nicknameAnswerMsg from the server
     * It manages the response to be shown to the user, including a transition
     * to the next window: game's lobby
     */
    private void manageNicknameAnswer(){
        if (answer.getNicknameStatus() == LoginNicknameAnswer.Status.FIRST_ACCEPTED) {
            numOfPlayersSelection.setOpacity(1);
            numOfPlayersSelection.setDisable(false);
            playerNickname.setDisable(true);
            numOfPlayersText.setOpacity(1);
            numOfPlayersSelection.setValue("2");

            textPane.setVisible(true);
            backButton.setDisable(true);
            backButton.setOpacity(1);
            loginResult.setText("Select number of players and click SUBMIT!");
            getOwner().setNickname(playerName);
        } else if (answer.getNicknameStatus() == LoginNicknameAnswer.Status.INVALID) {
            loginResult.setText("NICKNAME ALREADY CHOSEN! Please select another one");

        } else if (answer.getNicknameStatus() == LoginNicknameAnswer.Status.FULL_LOBBY) {
            loginResult.setText("GAME LOBBY IS FULL! Please wait");

        } else {
            getOwner().setNickname(playerName);
            C2SMessage lobbyUpdate = new LobbyUpdateRequest();
            getOwner().getServerHandler().sendMessageToServer(lobbyUpdate);

        }
    }

    public void manageKickedOut(){
        loginResult.setText("Sorry, there are some problems with the lobby you were inserted in. Insertion in a new lobby");
        playerNickname.setText(answerPlayerManagement.nickname);
        playerName = playerNickname.getText().toUpperCase();
        getOwner().setNickname(playerName);
        submitButton.setDisable(true);
        Thread delayingComputationThread = new Thread(this::delayComputation);
        delayingComputationThread.start();
    }

    /**
     * With this method user can press ENTER instead of
     * clicking the Submit button
     * @param e: event containing the key pressed
     */
    public void manageKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER) submitNicknameRequest();
    }

    public void closeTextPane() {
        textPane.setVisible(false);
    }

    private void delayComputation(){
        try {
                sleep(5000);
        } catch (InterruptedException e) {
                throw new RuntimeException(e);
        }
        submitButton.setDisable(false);
        C2SMessage nicknameRequest = new LoginNicknameRequest(playerName, true);
        getOwner().getServerHandler().sendMessageToServer(nicknameRequest);
    }
}
