package it.polimi.ingsw.Client.view.GUI;

import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.CommonGoalCard;
import it.polimi.ingsw.Server.Model.PersonalGoalCard;
import it.polimi.ingsw.Server.Model.Tile;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Controller implementation in which user can choose the column and the
 * order before inserting tiles picked into his/her shelf.
 * It contains user current shelf, goal cards and interactive
 * elements such as slider and buttons;
 * it also shows a recap of the turn at the end of it.
 * @see Controller
 */
public class ShelfController extends Controller {


    @FXML
    private Pane personalGoalCardPane;
    @FXML
    private Button commonGoalCardPane1;
    @FXML
    private Pane scoringPane_1;
    @FXML
    private Button commonGoalCardPane2;
    @FXML
    private Pane scoringPane_2;
    @FXML
    private FlowPane flowPane;
    @FXML
    private GridPane shelfPane;
    @FXML
    private Button helpButton;
    @FXML
    private Pane helpTextPane;
    @FXML
    private Pane infoCardPane;
    @FXML
    private Text infoCardText;
    @FXML
    private Pane infoCardImage;
    @FXML
    private Pane confirmationPane;
    @FXML
    private Pane errorPane;
    @FXML
    private Pane goalPane;
    @FXML
    private ImageView greenPGTick;
    @FXML
    private ImageView greenCGTick;
    @FXML
    private ImageView greenFSTick;
    @FXML
    private Text pointsEarned;
    @FXML
    private HBox columnSelection;

    @FXML
    private Pane chatPane;
    @FXML
    private TextArea chatText;
    @FXML
    private TextField chatMessage;

    public ChatRecordAnswer chatRecord;
    private Button startSwitch,endSwitch,columnButton;
    private int columnIdx;
    private InsertingTilesAnswer insertingTilesAnswer;
    private GoalAndScoreMsg goalAndScoreMsg;
    private Tile[][] shelf;
    private ArrayList<Tile> littleHand;

    public CommonGoalCard[] commonGoalCards;
    public PersonalGoalCard personalGoalCard;
    private volatile boolean isOpen, isCardOpen, isChatOpen;
    private ObservableList<Pane> tiles;
    private String[][] previewShelf;

    @FXML
    private void initialize() {
    }

    /**
     * This method sets all the elements from message parameters, in order to
     * have a customized window foreach player and foreach turn
     * @param message: ToShelfMsg
     */
    @Override
    public void build(S2CMessage message) {

        // initializing class parameters from ToShelfMessage
        ToShelfMsg toShelfMsg = (ToShelfMsg) message;
        this.shelf = toShelfMsg.getShelf();
        this.littleHand = toShelfMsg.getLittleHand();
        this.personalGoalCard = toShelfMsg.personalGoalCard;
        this.commonGoalCards = toShelfMsg.commonGoalCards;

        int howManyTiles = littleHand.size();

        // filling "tiles" with a Pane foreach littleHand's tiles
        tiles = FXCollections.observableArrayList();
        for (Tile tile : littleHand) {
            Pane pane = new Pane();
            pane.setId(tile.name());
            tiles.add(pane);
        }
        // "tiles" should keep size == 3 -> BLANK tiles to fill
        for (int i = howManyTiles; i < 3; i++) {
            Pane pane = new Pane();
            pane.setId("BLANK");
            tiles.add(pane);
        }

        // filling flow pane with littleHand's tiles
        ObservableList<Node> flowPaneChildren = flowPane.getChildren();

        for (int i = 0; i < 3; i++)
            flowPaneChildren.get(i).setId(tiles.get(i).getId());
        for (int i = 0; i < flowPaneChildren.size(); i++) {
            String id = flowPaneChildren.get(i).getId();
            if (Objects.equals(id, "BLANK")) {
                flowPaneChildren.remove(i);
                i--;
            }
        }
        flowPane.setMaxHeight(130*howManyTiles);
        flowPane.setPrefHeight(130*howManyTiles);

        // setting a matrix of String corresponding to player's shelf
        previewShelf = new String[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                previewShelf[i][j] = shelf[i][j].name();
            }
        }

        // initialising chosen column to default value
        columnIdx = 2;

        // setting the central shelf
        for (Node node: shelfPane.getChildren()) {
            String id = previewShelf[shelfPane.getRowIndex(node).intValue()][shelfPane.getColumnIndex(node).intValue()];
            node.setId(id);
        }
        updatePreview();

        // loading goal cards
        personalGoalCardPane.setId(personalGoalCard.getImageCode());
        commonGoalCardPane1.setId(commonGoalCards[0].getCardInfo().getName());
        scoringPane_1.setId("SCORE"+ toShelfMsg.commonPoints[0]);
        commonGoalCardPane2.setId(commonGoalCards[1].getCardInfo().getName());
        scoringPane_2.setId("SCORE"+ toShelfMsg.commonPoints[1]);

        // setting help and confirmation panes invisible
        helpTextPane.setVisible(false);
        infoCardPane.setVisible(false);
        chatPane.setVisible(false);
        confirmationPane.setVisible(false);

        // initialising switching buttons to null
        startSwitch = null;
        endSwitch = null;

    }

    /**
     * This method calls other 2 management methods
     * depending on the message received type
     * @param message: the server message
     */
    @Override
    public void receiveAnswer(S2CMessage message) {
        if(message instanceof InsertingTilesAnswer){
            insertingTilesAnswer = (InsertingTilesAnswer) message;
            manageInsertingTilesAnswer();
        }
        else if (message instanceof GoalAndScoreMsg) {
            goalAndScoreMsg = (GoalAndScoreMsg) message;
            manageGoalAndScoreMsg();
        }
        else if(message instanceof ChatRecordAnswer){
            chatRecord = (ChatRecordAnswer) message;
            manageChatAnswer();
        }
    }

    /**
     * This method is called when a chooseColumn button is clicked
     * It sets the chosenColumn value and updates the preview
     * @param event: event of clicking a button
     */
    public void selectColumn(ActionEvent event) {
        columnButton = (Button) event.getSource();
        try {
            columnIdx = Integer.parseInt(columnButton.getEllipsisString());
        } catch (NumberFormatException e) {
            System.out.println("Can't select column index");
            e.printStackTrace();
        }
        updatePreview();
    }

    /**
     * This method is called when user clicks buttons on the right.
     * It allows to switch tiles order clicking two buttons in sequence
     * @param actionEvent: event (clicking a button)
     */
    public void switchTiles(ActionEvent actionEvent) {

        if(startSwitch == null) {
            startSwitch = (Button) actionEvent.getSource();
        } else {
            endSwitch = (Button) actionEvent.getSource();
            // switching ids
            String temp = startSwitch.getId();
            startSwitch.setId(endSwitch.getId());
            endSwitch.setId(temp);

            startSwitch = null;
            endSwitch = null;
        }

        // updating tiles array (according to the new order)
        tiles.clear();
        for (Node node : flowPane.getChildren()) {
            Pane pane = new Pane();
            pane.setId(node.getId());
            tiles.add(pane);
        }

        updatePreview();
    }

    /**
     * This method creates a preview to show where the tiles will be
     * inserted in the shelf; this will help a lot the user to avoid
     * unwanted moves
     */
    @FXML
    private void updatePreview() {

        shelfPane.getChildren().removeIf(n -> n.getOpacity() == 0.5);

        int tilesNumber = littleHand.size();
        int r = 0;
        int c = columnIdx;
        int n = tilesNumber;
        while ( r<6 && Objects.equals(previewShelf[r][c],"BLANK") ) {
            r++;
        }
        if (r == 0)
            return;
        else
            r--;

        // adding #tilesNumber panes to user shelf (preview)
        for (int i = tilesNumber-1; i >= 0; i--) {
            Pane pane = clonePane(tiles.get(i));
            if (r < 0 || n < 0)
                break;
            pane.setPrefHeight(100);
            pane.setPrefWidth(100);
            pane.setOpacity(0.5);
            pane.setPickOnBounds(true);
            shelfPane.add(pane, c, r);
            r--; n--;
        }
    }

    /**
     * This method is called when "helpButton" is clicked.
     * It shows a text with instructions for the user
     */
    @FXML
    public void infoText() {

        if (isOpen) {
            helpButton.setId("off");
            helpTextPane.setVisible(false);
            isOpen = false;
        } else {
            helpButton.setId("on");
            helpTextPane.setVisible(true);
            isOpen = true;
        }

    }

    /**
     * This method is called when common goal cards are clicked.
     * It shows a pane containing the card clicked and a short text
     * that explains how to achieve the card
     */
    @FXML
    public void infoCard(ActionEvent e){
        if (isCardOpen) {
            isCardOpen = false;
            infoCardPane.setVisible(false);
        } else {
            Button buttonClicked = (Button) e.getSource();
            if(buttonClicked == commonGoalCardPane1)
                infoCardText.setText(commonGoalCards[0].getCardInfo().getDescription());
            else
                infoCardText.setText(commonGoalCards[1].getCardInfo().getDescription());
            infoCardImage.setId(buttonClicked.getId());
            infoCardPane.setVisible(true);
            isCardOpen = true;
        }
    }

    /**
     * This method is called when INSERT button is clicked
     * It shows a confirmation pane or an error if the chosen column is full
     */
    public void insert() {

        // checking if user is trying to insert tiles in a column without enough space
        if (!Objects.equals(shelf[littleHand.size() - 1][columnIdx], Tile.BLANK)) {
            // Showing an error popup
            errorPane.setVisible(true);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), errorPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            fadeOut.play();
                        }
                    },
                    500
            );

            return;
        }

        flowPane.setDisable(true);
        flowPane.setOpacity(1);

        columnSelection.setDisable(true);
        columnSelection.setOpacity(1);

        // setting confirmationPane visible
        confirmationPane.setVisible(true);
    }

    /**
     * This method is called when user confirm his/her choices.
     * It fills and send a insertingTilesMsg to the server
     */
    public void confirmChoice() {

        int tilesNumber = littleHand.size();

        int chosenIdx = columnIdx;

        // getting an array of indexes based on images order
        int[] chosenOrder = new int[tilesNumber];

        for (int i = 0; i < tilesNumber; i++) {
            String id = tiles.get(i).getId();
            Tile tile = Tile.valueOf(id);
            chosenOrder[tilesNumber-1 - i] = littleHand.indexOf(tile);
        }

        // handling color repetition in chosen tiles
        int mid = 1;
        while(tilesNumber != Arrays.stream(chosenOrder).distinct().toArray().length){
            if (chosenOrder[mid] == chosenOrder[mid - 1]) {
                chosenOrder[mid - 1]++;
                if(chosenOrder[mid-1] == chosenOrder.length) chosenOrder[mid-1] = 0;
            }
            if(tilesNumber == 3 && chosenOrder[mid+1] == chosenOrder[mid-1]){
                chosenOrder[mid-1]++;
                if(chosenOrder[mid-1] == chosenOrder.length) chosenOrder[mid-1] = 0;
            }
            if (tilesNumber == 3 && chosenOrder[mid] == chosenOrder[mid + 1]) {
                chosenOrder[mid + 1]++;
                if(chosenOrder[mid+1] == chosenOrder.length) chosenOrder[mid+1] = 0;
            }
        }

        // creating the insertingTilesMsg and send it
        C2SMessage insertingTilesMsg = new InsertingTilesMsg(chosenIdx, chosenOrder);
        getOwner().getServerHandler().sendMessageToServer(insertingTilesMsg);

    }

    /**
     * Shows an error if receives an invalid answer from the server
     */
    public void manageInsertingTilesAnswer() {

        if (!insertingTilesAnswer.valid) {

            // Showing an error popup
            errorPane.setVisible(true);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), errorPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            fadeOut.play();
                        }
                    },
                    500
            );
        }
    }

    /**
     * This method is called at the end of the turn, when server sends a
     * manageGoalAndScoreMsg. It shows a Pane with a brief recap of
     * what player has done during the turn
     */
    private void manageGoalAndScoreMsg() {

        goalPane.setVisible(true);
        if (goalAndScoreMsg.personalGoalAchieved)
            greenPGTick.setVisible(true);
        if (goalAndScoreMsg.commonGoalAchieved)
            greenCGTick.setVisible(true);
        if (goalAndScoreMsg.youFullyShelf)
            greenFSTick.setVisible(true);
        pointsEarned.setText(String.valueOf(goalAndScoreMsg.score));

    }

    /**
     * This method is called when user clicks "Finish Turn" button.
     * It creates and sends a finishTurnMsg to the server
     */
    public void sendFinishTurnMsg() {
        C2SMessage finishTurnMsg = new FinishTurnMsg();
        getOwner().getServerHandler().sendMessageToServer(finishTurnMsg);
    }

    public void closeConfirmationPane() {
        confirmationPane.setVisible(false);
        flowPane.setDisable(false);
        columnSelection.setDisable(false);
    }

    // -------------------- CHAT METHODS -------------------- //

    /**
     * This method is called when "chatButton" is clicked.
     * It shows a Pane with chat controls
     */
    public void openChat(){
        if (isChatOpen) {
            chatPane.setVisible(false);
            isChatOpen = false;
        } else {
            chatRefresh();
            chatPane.setVisible(true);
            isChatOpen = true;
        }
    }

    /**
     * This method sends a message to the server in order to update the current
     * chat, adding the newest messages to the chat pane
     */
    public void chatRefresh(){
        getOwner().getServerHandler().sendMessageToServer(new ChatRecordRequest(getOwner().getNickname()));
    }

    /**
     * This method creates an array list of messages
     * (current time + sender + msg content)
     * and sets chatText to this group of messages
     */
    public void manageChatAnswer(){
        ArrayList<String> messages = new ArrayList<>();
        //preparing the chat list
        for(int i=0; i<chatRecord.getChatStorage().getStorage().size(); i++){
            String sender = chatRecord.getChatStorage().getStorage().get(i).getSender();
            String content = chatRecord.getChatStorage().getStorage().get(i).getContent();
            LocalTime time = chatRecord.getChatStorage().getStorage().get(i).getLocalTime();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            //unifying message
            String message = "{"+time.format(timeFormatter)+"} "+sender+": "+content;
            messages.add(message+"\n");
        }
        String chat = String.valueOf(messages).replaceAll("\\[","").replaceAll("]","").replaceAll(",","");
        chatText.setText(chat);
    }

    /**
     * This method is called when a user try to send a message from the chat pane.
     * It checks if the first word of the message is @playerName in order to
     * send a private message or to broadcast it, then the method sends the message
     */
    public void sendMessage(){
        String message = chatMessage.getText();
        String receiver_name  = "EVERYONE";
        String formattedMsg = message;
        if (message == null) {
            message = "Sium";
            formattedMsg = "Sium";
        }
        ChatMsgRequest chatMsgRequest = new ChatMsgRequest(getOwner().getNickname(), receiver_name, formattedMsg);
        getOwner().getServerHandler().sendMessageToServer(chatMsgRequest);
        chatMessage.setText(null);
        chatRefresh();
    }

    /**
     * This method allows the user to press ENTER to send a message
     * @param e: event handling the key pressed
     */
    public void manageKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER) sendMessage();
    }


    /**
     * Utility method that creates a copy of a Pane
     * @param originalPane: source Pane
     * @return clonedPane: an exact copy of originalPane
     */
    public Pane clonePane(Pane originalPane) {
        Pane clonedPane = new Pane();

        // copying main properties from originalPane
        clonedPane.setStyle(originalPane.getStyle());
        clonedPane.setPrefSize(originalPane.getPrefWidth(), originalPane.getPrefHeight());
        clonedPane.setLayoutX(originalPane.getLayoutX());
        clonedPane.setLayoutY(originalPane.getLayoutY());
        clonedPane.setTranslateX(originalPane.getTranslateX());
        clonedPane.setTranslateY(originalPane.getTranslateY());
        clonedPane.setRotate(originalPane.getRotate());
        clonedPane.setScaleX(originalPane.getScaleX());
        clonedPane.setScaleY(originalPane.getScaleY());
        clonedPane.setOpacity(originalPane.getOpacity());
        clonedPane.setEffect(originalPane.getEffect());
        clonedPane.setId(originalPane.getId());

        return clonedPane;
    }

}
