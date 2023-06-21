package it.polimi.ingsw.Client.Gui;

/**
 * GUI view that prints the board, the common goal cards and the personal card of the player and his/her shelf,
 * it manages the picking of tiles from the board.
 * @author Matteo Lussana
 * @see Controller
 */

import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.CommonGoalCard;
import it.polimi.ingsw.Server.Model.PersonalGoalCard;
import it.polimi.ingsw.Server.Model.Tile;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;


public class GridController extends Controller{
    @FXML GridPane grid;
    @FXML GridPane shelfDisplay;
    @FXML GridPane joystick;
    @FXML Pane personal_card;
    @FXML Button common_card_1;
    @FXML Pane scoringPane_1;
    @FXML Button common_card_2;
    @FXML Pane scoringPane_2;
    @FXML Pane infoTextPane;
    @FXML Pane confirmationPane;
    @FXML HBox confirmationList;
    @FXML ImageView shelfFulledImage;

    @FXML VBox numOfTilesButtons;
    Button lastClickedButton = null;
    Button lastClickedButtonDir = null;
    Button lastClickedButtonNum = null;
    ArrayList<Button> possibleWay = new ArrayList<>();
    String direction = "W";
    int numberOfTiles = 1;

    Integer row = null;
    Integer column = null;
    Boolean isRulesOpen = false;
    Boolean isChatOpen = false;

    //rule info
    @FXML Text infoText;
    @FXML Button infoButton;

    //error info
    @FXML Pane errorPane;
    @FXML Text errorText;

    //card info
    @FXML Pane infoCardPane;
    @FXML Text infoCardText;
    @FXML Pane imageInfoCard;

    @FXML Pane chatPane;
    @FXML TextArea chatText;
    @FXML TextField chatMessage;
    Boolean isCardOpen = false;

    public int maxTilesPickable;
    public Tile[][] boardSnapshot;
    public Tile[][] shelfSnapshot;
    public CommonGoalCard[] commonGoalCards;
    public PersonalGoalCard personalGoalCard;
    public YourTurnMsg yourTurnMsg;
    private InitialPositionAnswer initialPositionAnswer;
    private PlayerChoiceAnswer playerChoiceAnswer;
    public ChatRecordAnswer chatRecord;
    private Boolean gameIsFinishing;

    /**
     * Message constructor method
     * @param message: message from the server that contains the player's nickname, his/her personal card and his/her shelf,
     *                 the board, the common cards and the information about the ending of game
     */
    @Override
    public void build(S2CMessage message) {
        yourTurnMsg = (YourTurnMsg) message;
        this.boardSnapshot = yourTurnMsg.boardSnapshot;
        this.shelfSnapshot = yourTurnMsg.shelfSnapshot;
        this.commonGoalCards = yourTurnMsg.commonGoalCards;
        this.personalGoalCard = yourTurnMsg.personalGoalCard;
        this.maxTilesPickable = yourTurnMsg.maxTilesPickable;
        this.gameIsFinishing = yourTurnMsg.isOver;
        setStart();
    }

    /**
     * Preparing method
     * This method fills all the node grid with the tiles contained in the board and in the shelf, set the visibility of the infoPane
     * and assign the right score at the CommonGoalCard
     */
    public void setStart(){
        //filling the shelf
        for(Node node : shelfDisplay.getChildren()){
            String id = shelfSnapshot[GridPane.getRowIndex(node)][GridPane.getColumnIndex(node)].name();
            node.setId(id);
        }
        //filling the board
        for(Node node : grid.getChildren()){
            String id = boardSnapshot[grid.getRowIndex(node).intValue()][grid.getColumnIndex(node).intValue()].name();
            node.setId(id);
        }
        //coloring the default direction (W) and numbers of tiles (1) buttons
        for(Node node : numOfTilesButtons.getChildren()){
            if(node.getId().charAt(node.getId().length()-1) == '1') ((Button) node).setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        }
        for(Node node : joystick.getChildren()){
            if(((Button) node).getText() == "W") ((Button) node).setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        }
        //setting the personal card
        personal_card.setId(personalGoalCard.getImageCode());
        //setting the common cards and their relative score
        common_card_1.setId(commonGoalCards[0].getCardInfo().getName());
        scoringPane_1.setId("SCORE"+yourTurnMsg.commonPoints[0]);
        common_card_2.setId(commonGoalCards[1].getCardInfo().getName());
        scoringPane_2.setId("SCORE"+yourTurnMsg.commonPoints[1]);
        //setting the finish game tile
        if(gameIsFinishing) shelfFulledImage.setVisible(false);
        //hide info panes
        errorPane.setVisible(false);
        infoTextPane.setVisible(false);
        confirmationPane.setVisible(false);
        infoCardPane.setVisible(false);
        //setting text of rule pane
        infoText.setText("It's time to pick tiles from the board! Choose the position of the first tile clicking on the board, " +
                "then, if you want to pick other tiles, you must select the direction in which you want to choose them and how many tiles you want!\n" +
                      "Remember that every tile you want to pick must have at least one 'free side': without an adjacent tile!");
        chatPane.setVisible(false);
        //chatRefresh();
    }
    /**
     * this method manage the click of a button in order to make appear and disappear the rules pane
     */

    public void infoText(){
        if (isRulesOpen) {
            infoButton.setId("off");
            infoTextPane.setVisible(false);
            isRulesOpen = false;
        } else {
            infoButton.setId("on");
            infoTextPane.setVisible(true);
            isRulesOpen = true;
        }
    }

    /**
     * this method manage the click of a button in order to make appear and disappear the commonCard pane, the personalCard pane
     */
    public void infoCard(ActionEvent e){
        if (isCardOpen) {
            isCardOpen = false;
            infoCardPane.setVisible(false);
        } else {
            Button buttonClicked = (Button) e.getSource();
            imageInfoCard.setId(buttonClicked.getId());
            if(buttonClicked == common_card_1){
                infoCardText.setText(commonGoalCards[0].getCardInfo().getDescription());
            }
            else{
                infoCardText.setText(commonGoalCards[1].getCardInfo().getDescription());
            }
            infoCardPane.setVisible(true);
            isCardOpen = true;
        }
    }

    /**
     * this method manages the choosing of the starting tile by the player, its resets the color of the tile chose previously,
     * changes the color of the tile clicked and update the preview of the path (calling showPath())
     */
    public void chooseStartingTile(ActionEvent e){
        if (lastClickedButton != null) {
            lastClickedButton.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0))));
        }
        Button button = (Button)e.getSource();
        lastClickedButton = button;
        button.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        row = GridPane.getRowIndex(button);
        column = GridPane.getColumnIndex(button);
        showPath();
    }

    /**
     * this method manages the choosing of the direction of picking by the player, its resets the color of the direction chose previously,
     * changes the color of the direction clicked and update the preview of the path (calling showPath())
     */
    public void getDirection(ActionEvent e){
        if (lastClickedButtonDir != null) {
            lastClickedButtonDir.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0))));
        }
        Button button = (Button)e.getSource();
        lastClickedButtonDir = button;
        button.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        Integer y = GridPane.getRowIndex(button);
        Integer x = GridPane.getColumnIndex(button);
        for(Node node : joystick.getChildren()){
            if(joystick.getRowIndex(node) == y && joystick.getColumnIndex(node) == x) this.direction = ((Button) node).getText();
        }
        showPath();
    }

    /**
     * this method manages the click of the buttons of the number of tiles that the player is picking
     */
    public void setNumberOfTiles(ActionEvent e){
        String valueString = ((Button) e.getSource()).getId();
        char valueChar = valueString.charAt(valueString.length()-1);
        numberOfTiles = valueChar - '0';
        showPath();
        setNumTilesColor(numberOfTiles);
    }

    /**
     * this method manages the colors of the buttons of the number of tiles that the player is picking
     */
    public void setNumTilesColor(int numberOfTiles){
        if (lastClickedButtonNum != null) {
            lastClickedButtonNum.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0))));
        }
        for(Node node : numOfTilesButtons.getChildren()){
            String id = "numTiles"+numberOfTiles;
            if(Objects.equals(node.getId(), id)){
                Button button = (Button)node;
                lastClickedButtonNum = button;
                button.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));
            }
        }
    }

    /**
     * this method manages preview of the tiles that the player will pick if he/she press the pick button,
     * it resets the previous path and colors the border of the new one (the tile are saved in an array called possibleWay calling changeRoad())
     */
    @FXML
    public void showPath(){
        if(row!=null && column!=null){
            if(!possibleWay.isEmpty()){
                for(Button button : possibleWay)
                    button.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0))));
                possibleWay.removeAll(possibleWay);
            }
            int value = numberOfTiles;
            for (Node node : grid.getChildren()) {
                if (grid.getRowIndex(node) == row && grid.getColumnIndex(node) == column) {
                    changeRoad(node, direction, value);
                }
            }
            for(Button button : possibleWay)
                button.setBorder(new Border(new BorderStroke(Color.BROWN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));
        }
    }

    /**
     * this method update the possibleWay array, which contains the tiles that the player will pick with the actual option of starting position, direction and number of tiles.
     * it manages also the picking of invalid tiles (out of border or empty) by forcing the decreasing slider value
     */
    public void changeRoad(Node startTile, String direction, int value){
        if(value == 1) {
            possibleWay.add((Button)startTile);
        }
        else{
            int contatore = 0;
            int x= column;
            int y = row;
            while (contatore < value) {
                for (Node node : grid.getChildren())
                    if(grid.getRowIndex(node) == y && grid.getColumnIndex(node) == x) {
                        if(node.getId().equals("BLANK") || node.getId().equals("NOT_VALID")){
                            setError("Cell not valid, change the route or reduce the number of tiles");
                            value=contatore;
                            setNumTilesColor(value);
                        }
                        else if(x > 8 || x < 0 || y > 8 || y < 0){
                           setError("Cell out of border, change the route or reduce the number of tiles");
                            value=contatore;
                            setNumTilesColor(value);
                        }
                        else {
                            possibleWay.add((Button) node);
                        }
                    }
                switch (direction) {
                    case "N": y--; contatore++;
                        break;
                    case "S": y++; contatore++;
                        break;
                    case "E": x++; contatore++;
                        break;
                    case "W": x--; contatore++;
                        break;
                }
            }
        }
    }

    /**
     * this method manages the appearing of the error pane which is set with a specific error string
     * it appears and after an amount of time disappears automatically
     */
    public void setError(String error){
        errorText.setText(error);
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

    /**
     * this method manages the confirmation of the tiles in the preview (possibleWay),
     * its makes appear a pane which shows the tiles and ask for a confirmation
     */
    public void confirmChoice(){
        if(!possibleWay.isEmpty()){
            for(Node pane : confirmationList.getChildren())
                pane.setId("BLANK");
            for(int i=0; i< possibleWay.size(); i++){
                confirmationList.getChildren().get(i).setId(possibleWay.get(i).getId());
            }
            confirmationPane.setVisible(true);
        }
        else setError("You have to select some cell in order to continue");
    }
    public void closeConfirmationPane(){
        confirmationPane.setVisible(false);
    }

    /**
     * this method sends message to server with the initial position chosen by the player in order to be checked by the server
     * this method starts a chain of request and answer between the client and the server that, if the tiles picked are valid, will
     * end with the picking of tiles from board
     */
    public void checkPosition(){
        C2SMessage initialPosition = new InitialPositionMsg(row, column);
        getOwner().getServerHandler().sendMessageToServer(initialPosition);
        confirmationPane.setVisible(false);
    }

    /**
     * this method manages the reception of messages from server sorting them by type
     */
    @Override
    public void receiveAnswer(S2CMessage message) {
        if(message instanceof InitialPositionAnswer){
            initialPositionAnswer = (InitialPositionAnswer) message;
            manageInitialPositionAnswer();
        }
        else if(message instanceof PlayerChoiceAnswer){
            playerChoiceAnswer = (PlayerChoiceAnswer) message;
            managePickTilesAnswer();
        }
        else if(message instanceof ChatRecordAnswer){
            chatRecord = (ChatRecordAnswer) message;
            manageChatAnswer();
        }
    }

    /**
     * this method manages the reception of initial position message,
     * if the initial position is valid it sends the PlayerChoiceMSG to the server in order to check the chosen tiles,
     * if the initial position is not valid it shows the error pane
     */
    public void manageInitialPositionAnswer(){
        if(initialPositionAnswer.valid) {
            C2SMessage pickTiles = new PlayerChoiceMsg(row, column, (direction.toLowerCase()).charAt(0), possibleWay.size()-1, yourTurnMsg.maxTilesPickable);
            getOwner().getServerHandler().sendMessageToServer(pickTiles);
        }
        else{
            setError("all the selected Tile must have a free near space and you must have enough space in your shelf");
        }
    }

    /**
     * this method manages the reception of initial position message,
     * if the chosen tiles are not valid it shows the error pane
     */
    public void managePickTilesAnswer(){
        if(!playerChoiceAnswer.valid){
            setError("all the selected Tile must have a free near space and you must have enough space in your shelf");
        }
    }

    // -------------------- CHAT METHODS -------------------- //

    /**
     * this method manage the click of a button in order to make appear and disappear the chat pane
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
        messages.add("To send a private message tag the player (@playerName) at the beginning\n");
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
        if(message.startsWith("@")){
            receiver_name = message.split(" ")[0];
            StringBuilder sb = new StringBuilder(receiver_name);
            sb.deleteCharAt(0);
            receiver_name = sb.toString();
            formattedMsg = message.substring(receiver_name.length()+2);
            if(receiver_name.toUpperCase().equals(getOwner().getNickname())) receiver_name = "";
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
}

