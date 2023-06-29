package it.polimi.ingsw.Client.view.GUI;

import it.polimi.ingsw.Server.Messages.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

/**
 * GUI view which is shown between turns, while other player are playing
 * @author Matteo Lussana
 * @see Controller
 */
public class WaitingController extends Controller {
    @FXML GridPane grid1;
    @FXML GridPane grid2;
    @FXML
    Text currentPlayerText;

    @FXML
    Pane chatPane;
    @FXML
    TextArea chatText;
    @FXML
    TextField chatMessage;

    public ChatRecordAnswer chatRecord;

    Boolean isChatOpen = false;


    /**
     * Message constructor method
     * @param message: goWaitingGUI message from the server
     */
    @Override
    public void build(S2CMessage message) {
        if (message instanceof GameIsEndingUpdate)
            manageGameIsEnding( (GameIsEndingUpdate) message );
        chatPane.setVisible(false);
    }

    /**
     * this method manages the reception of messages from server sorting them by type
     */
    @Override
    public void receiveAnswer(S2CMessage message) {
        if (message instanceof GameIsEndingUpdate)
            manageGameIsEnding( (GameIsEndingUpdate) message );
        else if(message instanceof ChatRecordAnswer){
            chatRecord = (ChatRecordAnswer) message;
            manageChatAnswer();
        }
    }

    // -------------------- PANE METHODS -------------------- //
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

    // -------------------- MANAGE MESSAGE METHODS -------------------- //
    /**
     * this method manages the reception of gameIsEndingUpdate,
     * this message is received wen the player has played his last turn, so now
     * he has to wait that all players complete their turn
     * the method show a message with the name of the player that is playing
     */
    private void manageGameIsEnding(GameIsEndingUpdate message) {
        chatPane.setVisible(false);
        if(message.firstToFinish == message.playerIndex)
            currentPlayerText.setText("Congratulation, you are the first one to fill the personal shelf! The game is ending...");
        else
            currentPlayerText.setText(message.players[message.firstToFinish]+" is the first one to fill the personal shelf.The game is ending...");
        if(message.gameOver){
            //playerIndex needed to choose the right clientHandler
            C2SMessage endGameMsg = new EndGameMsg(message.playerIndex);
            getOwner().getServerHandler().sendMessageToServer(endGameMsg);
        }
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

    // -------------------- CHAT METHODS -------------------- //
    /**
     * This method is called when a user try to send a message from the chat pane.
     * It checks if the first word of the message is @playerName in order to
     * send a private message or to broadcast it, then the method sends the message
     */
    public void sendMessage(){
        String message = chatMessage.getText();
        String receiver_name  = "EVERYONE";
        String formattedMsg = message;
        if(message == null) {
            message = "KEKW";
            formattedMsg = "KEKW";
        }
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

    /**
     * This method sends a message to the server in order to update the current
     * chat, adding the newest messages to the chat pane
     */
    public void chatRefresh(){
        getOwner().getServerHandler().sendMessageToServer(new ChatRecordRequest(getOwner().getNickname()));
    }

    // -------------------- EASTER EGG METHODS -------------------- //
    /**
     * This method manage the click of a button in order to change the order of tiles in side gridPane
     */
    public void AnimationGrid(){
        String[] names = {"BLUE", "GREEN", "WHITE", "PINK", "YELLOW", "LIGHTBLUE"};

        Random random = new Random();

        int num = random.nextInt(5);
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

