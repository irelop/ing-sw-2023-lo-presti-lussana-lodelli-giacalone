package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.Server.Model.PatternStrategy.DPatternStrategy;
import it.polimi.ingsw.utils.ReadFileByLines;
import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DPatternStrategy class
 * @author Riccardo Lodelli
 */
class DPatternStrategyTest {

    StrategyInterface strategy;

    Tile[][] correctPattern = null;

    Tile[][] wrongPattern =  null;
    Tile[][] thirdPattern =  null;

    @BeforeEach
    void setUp() {
        strategy = new DPatternStrategy();
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/DPattern.txt");

        correctPattern = new Tile[6][5];
        wrongPattern = new Tile[6][5];
        thirdPattern = new Tile[6][5];

        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++) {
                correctPattern[i][j] = Tile.valueOf(values[j]);
            }
        }

        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++) {
                wrongPattern[i][j] = Tile.valueOf(values[j]);
            }
        }


        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++) {
                thirdPattern[i][j] = Tile.valueOf(values[j]);
            }
        }
    }

    /**
     * Test for 5 tiles forming a diagonal from n-w to s-e.
     * Test should ensure that checkPattern returns true
     */
    @Test
    void checkPattern_true() {
        assertTrue(strategy.checkPattern(correctPattern));
    }

    /**
     * Test for a shelf without any diagonal pattern.
     * Test should ensure that checkPattern returns false
     */
    @Test
    void checkPattern_false() {
        assertFalse(strategy.checkPattern(wrongPattern));
    }

    /**
     * Test for 5 tiles forming a diagonal from s-w to n-e.
     * Test should ensure that checkPattern returns true
     */
    @Test
    void checkPattern_otherSideTrue() {
        assertTrue(strategy.checkPattern(thirdPattern));
    }
}