package it.polimi.ingsw.Client.Gui;


import it.polimi.ingsw.Server.Messages.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.Random;

public class LastPlayerConnectedController extends Controller {
    @FXML Text alertText;

    LastOneConnectedMsg lastOneConnectedMsg;

    private boolean goOn = true;

    Countdown countdown;


    @Override
    public void build(S2CMessage message) {
        lastOneConnectedMsg = (LastOneConnectedMsg) message;
        alertText.setText("\n"+lastOneConnectedMsg.nickname+", all the other players have disconnected.\n" +
                "At least one of them has to reconnect to this game in 30 seconds or you will be the winner.\n");
        this.countdown = new Countdown();
        this.countdown.controller = this;
        this.countdown.run();
    }

    @Override
    public void receiveAnswer(S2CMessage message) {
        if (message instanceof ReconnectionNotifyMsg)
            manageReconnection();
    }

    public void manageReconnection(){
         this.countdown.goOn = false;
    }





}

