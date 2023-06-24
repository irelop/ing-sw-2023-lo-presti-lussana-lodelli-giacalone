package it.polimi.ingsw.Client.Gui;


import it.polimi.ingsw.Server.Messages.*;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import static java.lang.Thread.sleep;

/**
 * GUI view which appears when all players but one are disconnected
 * @author Matteo Lussana
 * @see Controller
 */
public class LastPlayerConnectedController extends Controller{
    @FXML Text alertText;

    LastOneConnectedMsg lastOneConnectedMsg;
    private boolean goOn = false;

    /**
     * Message constructor method
     * @param message: message from the server that contains the player's nickname
     */
    @Override
    public void build(S2CMessage message) {
        Thread thread = new Thread(this::timer);
        thread.start();
        lastOneConnectedMsg = (LastOneConnectedMsg) message;
        alertText.setText("\n"+lastOneConnectedMsg.nickname+", all the other players have disconnected.\n" +
                "At least one of them has to reconnect to this game in 30 seconds or you will be the winner.\n");
    }

    /**
     * this method manages the reception of messages from server
     */
    @Override
    public void receiveAnswer(S2CMessage message) {
        if (message instanceof ReconnectionNotifyMsg)
            manageReconnection();
    }

    /**
     * Method called when one of the disconnected players reconnect, this method will stop the thread that
     * is counting the time
     */
    public void manageReconnection(){
         this.goOn = true;
    }

    /**
     * Method called when the thread that was counting down 30sec has finished and no one reconnect
     * it shows the pane with a win message and then send a message to the server that will close the game
     */
    public void finish(){
        alertText.setText("Congratulation! You are the winner!");
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        FinishGameRequest finishGameRequest = new FinishGameRequest();
        getOwner().getServerHandler().sendMessageToServer(finishGameRequest);
    }

    /**
     * Method used by a thread in order to count 30sec before end the game, giving
     * to the disconnected players the possibility to reconnect to the game
     */
    public void timer(){
        for(int i=30; i>=0 && !goOn; i--){
            System.out.println(i+" seconds");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(!goOn){
            finish();
        }
    }
}

