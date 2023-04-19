package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.Model.PatternStrategy.M8PatternStrategy;
import it.polimi.ingsw.Model.StrategyInterface;
import it.polimi.ingsw.Model.Tile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

class M8PatternStrategyTest {

    StrategyInterface m8PatternStrategy = null;

    @BeforeEach
   void setUp() {
        m8PatternStrategy = new M8PatternStrategy();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void checkPattern_true() {

        Tile[][] test = {
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.GREEN, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.GREEN, Tile.YELLOW, Tile.BLANK, Tile.BLANK, Tile.GREEN},
                {Tile.GREEN, Tile.YELLOW, Tile.BLUE, Tile.GREEN, Tile.GREEN},
                {Tile.LIGHTBLUE, Tile.BLUE, Tile.GREEN, Tile.GREEN, Tile.YELLOW}
        };
        assertTrue(m8PatternStrategy.checkPattern(test));

    }


    @Test
    void checkPattern_false() {

        Tile[][] test = {
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.BLANK, Tile.GREEN, Tile.BLANK, Tile.BLANK}
        };
        assertFalse(m8PatternStrategy.checkPattern(test));

    }

    @Test
    void checkPattern_true2() {

        Tile[][] test = {
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE},
                {Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE},
                {Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE},
                {Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE}
        };
        assertTrue(m8PatternStrategy.checkPattern(test));

    }

    @Test
    void checkPattern_false2() {

        Tile[][] test = {
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK, Tile.BLANK},
                {Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE, Tile.LIGHTBLUE},
                {Tile.YELLOW, Tile.BLUE, Tile.BLUE, Tile.GREEN, Tile.BLUE},
                {Tile.WHITE, Tile.WHITE, Tile.WHITE, Tile.YELLOW, Tile.YELLOW},
                {Tile.WHITE, Tile.YELLOW, Tile.GREEN, Tile.GREEN, Tile.BLUE},
        };
        assertFalse(m8PatternStrategy.checkPattern(test));

    }
}