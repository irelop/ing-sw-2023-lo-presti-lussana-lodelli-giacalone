package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.Server.Model.PatternStrategy.V6DPatternStrategy;
import it.polimi.ingsw.utils.ReadFileByLines;
import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for V6DPatternStrategy class
 * @author Andrea Giacalone
 */
class V6DPatternStrategyTest {
    StrategyInterface commonStrategy;
    Tile[][] correctPattern = null;
    Tile[][] wrongPattern = null;



    @BeforeEach
    void setUp() {
        commonStrategy = new V6DPatternStrategy();
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/v6dPS.txt");

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
     * Test for shelf containing 3 columns filled with tiles of max
     * 3 different colors (1st, 3rd, 5th).
     * Test should ensure that checkPattern returns true
     */
    @Test
    void checkCorrectPattern_ShouldAssertTrue() {
        assertTrue(commonStrategy.checkPattern(correctPattern));
    }

    /**
     * Test for shelf containing ONLY 2 columns filled with tiles of max
     * 3 different colors (3rd column now has 4 different colors).
     * Test should ensure that checkPattern returns false
     */
    @Test
    void checkWrongPattern_ShouldAssertFalse() {
        assertFalse(commonStrategy.checkPattern(wrongPattern));
    }

}