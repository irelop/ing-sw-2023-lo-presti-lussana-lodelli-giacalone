package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class APatternStrategyTest {

    StrategyInterface aPatternStrategy = null;
    Tile[][] shelf = null;

    @Before
    public void setUp() {
        aPatternStrategy = new APatternStrategy();
        shelf = new Tile[6][5];

        for(int i=0; i<6; i++)
            for(int j=0; j<5; j++)
                shelf[i][j] = Tile.BLANK;

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkPattern_correctInput_correctOutput_true() {
        shelf[0][0] = Tile.BLUE;
        shelf[5][0] = Tile.BLUE;
        shelf[0][4] = Tile.BLUE;
        shelf[5][4] = Tile.BLUE;

        assertTrue(aPatternStrategy.checkPattern(shelf));

    }

    @Test
    public void checkPattern_correctInput_correctOutput_false() {

        shelf[3][3] = Tile.BLUE;
        assertFalse(aPatternStrategy.checkPattern(shelf));

    }

    @Test
    public void checkPattern_correctInput_correctOutput_false2() {
        shelf[0][0] = Tile.BLUE;
        shelf[5][0] = Tile.BLUE;
        shelf[0][4] = Tile.YELLOW;
        shelf[5][4] = Tile.BLUE;

        assertFalse(aPatternStrategy.checkPattern(shelf));

    }

}