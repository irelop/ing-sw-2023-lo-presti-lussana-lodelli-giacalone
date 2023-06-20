package it.polimi.ingsw.Client.Gui;

/**
 * GUI view which is shown between turns, while other player are playeing
 * @author Matteo Lussana
 * @see Controller
 */

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



    @Override
    public void build(S2CMessage message) {
        if (message instanceof GameIsEndingUpdateAnswer)
            manageGameIsEnding( (GameIsEndingUpdateAnswer) message );
        chatPane.setVisible(false);
        //chatRefresh();
    }

    @Override
    public void receiveAnswer(S2CMessage message) {
        if (message instanceof GameIsEndingUpdateAnswer)
            manageGameIsEnding( (GameIsEndingUpdateAnswer) message );
        else if(message instanceof ChatRecordAnswer){
            chatRecord = (ChatRecordAnswer) message;
            manageChatAnswer();
        }
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

    public void sendMessage(){
        String message = chatMessage.getText().toLowerCase();
        String receiver_name  = "EVERYONE";
        if(message.startsWith("@")){
            receiver_name = message.split(" ")[0];
            receiver_name.replaceAll("@", "");
            if(receiver_name.toUpperCase() == getOwner().getNickname()) receiver_name = "";
        }
        ChatMsgRequest chatMsgRequest = new ChatMsgRequest(getOwner().getNickname(), receiver_name, message);
        getOwner().getServerHandler().sendMessageToServer(chatMsgRequest);
        chatMessage.setText(null);
        chatRefresh();
    }
    public void manageKeyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER) sendMessage();
    }

    public void chatRefresh(){
        getOwner().getServerHandler().sendMessageToServer(new ChatRecordRequest(getOwner().getNickname()));
    }
}

