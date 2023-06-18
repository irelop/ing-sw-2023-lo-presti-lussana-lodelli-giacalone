package it.polimi.ingsw.Client.Gui;


import it.polimi.ingsw.Server.Messages.*;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class LastPlayerConnectedController extends Controller{
    @FXML Text alertText;

    LastOneConnectedMsg lastOneConnectedMsg;
    private boolean goOn = false;



    @Override
    public void build(S2CMessage message) {
        Thread thread = new Thread(()->timer());
        thread.start();
        lastOneConnectedMsg = (LastOneConnectedMsg) message;
        alertText.setText("\n"+lastOneConnectedMsg.nickname+", all the other players have disconnected.\n" +
                "At least one of them has to reconnect to this game in 30 seconds or you will be the winner.\n");
    }

    @Override
    public void receiveAnswer(S2CMessage message) {
        if (message instanceof ReconnectionNotifyMsg)
            manageReconnection();
    }


    public void manageReconnection(){
         this.goOn = true;
    }

    public void wakeUp(){
        alertText.setText("Congratulation! You are the winner!");
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        FinishGameRequest finishGameRequest = new FinishGameRequest();
        getOwner().getServerHandler().sendMessageToServer(finishGameRequest);
    }

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
            wakeUp();
        }
    }
}

