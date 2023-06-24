package it.polimi.ingsw.Client.CLI;

import it.polimi.ingsw.Client.view.CLI.EndGameView;
import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class EndGameViewTest {
    ArrayList<String> player = new ArrayList<>();
    ArrayList<Integer> score = new ArrayList<>();


    ScoreBoardMsg msg;

    EndGameView view;
    @BeforeEach
    void setUp() {
        player.add("matteo");
        player.add("power");
        player.add("kekkobomba");
        player.add("cicciogamer");

        score.add(40);
        score.add(30);
        score.add(10);
        score.add(20);

        msg = new ScoreBoardMsg(player, score, "player1", false);
        view = new EndGameView(msg);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void visual_test(){
        view.run();
    }

}