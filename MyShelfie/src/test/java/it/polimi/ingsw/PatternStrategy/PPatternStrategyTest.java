package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.Server.Model.PatternStrategy.PPatternStrategy;
import it.polimi.ingsw.utils.ReadFileByLines;
import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PPatternStrategy class
 * @author Andrea Giacalone
 */
class PPatternStrategyTest {
    StrategyInterface commonStrategy;
    Tile[][] correctPatternWest;
    Tile[][] correctPatternEast;
    Tile[][] wrongPattern;



    @BeforeEach
    void setUp() {
        commonStrategy = new PPatternStrategy();
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/pPS.txt");

        correctPatternWest = new Tile[6][5];
        correctPatternEast = new Tile[6][5];
        wrongPattern = new Tile[6][5];
        //- - - - - - - - - - - - - - - - - - --  - - - - - - - - - - - - - - - - - -
        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++) {
                correctPatternWest[i][j] = Tile.valueOf(values[j]);
            }
        }
        //- - - - - - - - - - - - - - - - - - --  - - - - - - - - - - - - - - - - - -
        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++) {
                correctPatternEast[i][j] = Tile.valueOf(values[j]);
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
        correctPatternWest = null;
        correctPatternEast = null;
        wrongPattern = null;
    }

    /**
     * Test for shelf "half" filled, creating a pyramid from n-w to s-e.
     * Test should ensure that checkPattern returns true
     */
    @Test
    void checkCorrectPatternFromWest_ShouldAssertTrue() {
        assertTrue(commonStrategy.checkPattern(correctPatternWest));
    }


    /**
     * Test for shelf "half" filled, creating a pyramid from n-e to s-w.
     * Test should ensure that checkPattern returns true
     */
    @Test
    void checkCorrectPatternFromEast_ShouldAssertTrue() {
        assertTrue(commonStrategy.checkPattern(correctPatternEast));
    }

    /**
     * Test for shelf "half" filled, but not creating a pyramid.
     * Test should ensure that checkPattern returns false
     */
    @Test
    void checkWrongPattern_ShouldAssertFalse() {
        assertFalse(commonStrategy.checkPattern(wrongPattern));
    }
}