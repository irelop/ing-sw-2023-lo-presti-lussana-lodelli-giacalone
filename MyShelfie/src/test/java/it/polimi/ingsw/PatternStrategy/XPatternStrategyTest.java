package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.Server.Model.PatternStrategy.XPatternStrategy;
import it.polimi.ingsw.utils.ReadFileByLines;
import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for XPatternStrategy class
 * @author Andrea Giacalone
 */
class XPatternStrategyTest {
    StrategyInterface commonStrategy;
    Tile[][] correctPattern = null;
    Tile[][] wrongPattern = null;



    @BeforeEach
    void setUp() {
        commonStrategy = new XPatternStrategy();
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/xPS.txt");

        correctPattern = new Tile[6][5];
        wrongPattern = new Tile[6][5];
//- - - - - - - - - - - - - - - - - - --  - - - - - - - - - - - - - - - - - -
        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++) {
                correctPattern[i][j] = Tile.valueOf(values[j]);
            }
        }
//- - - - - - - - - - - - - - - - - - --  - - - - - - - - - - - - - - - - - -
        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++) {
                wrongPattern[i][j] = Tile.valueOf(values[j]);
            }
        }



    }

    @AfterEach
    void tearDown() {
        correctPattern = null;
        wrongPattern = null;
    }

    /**
     * Test for shelf containing 5 tiles of the same color forming a "X"
     * Test should ensure that checkPattern returns true
     */
    @Test
    void checkCorrectPattern_ShouldAssertTrue() {
        assertTrue(commonStrategy.checkPattern(correctPattern));
    }

    /**
     * Test for shelf that DOESN'T contain the "X" pattern
     * Test should ensure that checkPattern returns false
     */
    @Test
    void checkWrongPattern_ShouldAssertFalse() {
        assertFalse(commonStrategy.checkPattern(wrongPattern));
    }

}