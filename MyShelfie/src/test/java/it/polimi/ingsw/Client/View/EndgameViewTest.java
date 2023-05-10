package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;
import it.polimi.ingsw.Server.Model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import it.polimi.ingsw.Server.Model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndgameViewTest {
    ArrayList<String> player = new ArrayList<>();
    ArrayList<Integer> score = new ArrayList<>();


    ScoreBoardMsg msg;

    EndgameView view;
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

        msg = new ScoreBoardMsg(player, score);
        view = new EndgameView(msg);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void visual_test(){
        view.run();
    }

}