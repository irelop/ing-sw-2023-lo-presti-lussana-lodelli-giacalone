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
    ArrayList<Player> player = new ArrayList<>();
    ArrayList<Integer> score = new ArrayList<>();

    Player p1 = new Player("matteo");
    Player p2 = new Player("Pow3r");
    Player p3 = new Player("kekkobomba");
    Player p4 = new Player("CiccioGamer89");


    ScoreBoardMsg msg;

    EndgameView view;
    @BeforeEach
    void setUp() {
        player.add(p1);
        player.add(p2);
        player.add(p3);
        player.add(p4);

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