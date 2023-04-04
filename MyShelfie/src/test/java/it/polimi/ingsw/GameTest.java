package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    Game game;
    ArrayList<Player> players;

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new Player("player1"));
        players.add(new Player("player2"));
        game = new Game(players);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void setChair_test(){

    }
}