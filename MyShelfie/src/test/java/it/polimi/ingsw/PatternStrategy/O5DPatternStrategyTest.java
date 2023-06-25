package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.Server.Model.PatternStrategy.O5DPatternStrategy;
import it.polimi.ingsw.utils.ReadFileByLines;
import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for O5DPatternStrategy class
 * @author Andrea Giacalone
 */
class O5DPatternStrategyTest {

    StrategyInterface O5DPatternStrategy;
    Tile[][] correctPattern = null;
    Tile[][] wrongPattern = null;



    @BeforeEach
    void setUp() {
        O5DPatternStrategy = new O5DPatternStrategy();
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/O5DPattern.txt");

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
     * Test for shelf with 4 rows filled with tiles of max 3 colors.
     * Test should ensure that checkPattern returns true
     */
    @Test
    void checkCorrectPattern_ShouldAssertTrue() {
        assertTrue(O5DPatternStrategy.checkPattern(correctPattern));
    }

    /**
     * Test for shelf WITHOUT 4 rows filled with tiles of max 3 colors
     * (There are 2 lines with BLANK tiles and 1 line with 4 colors).
     * Test should ensure that checkPattern returns false
     */
    @Test
    void checkWrongPattern_ShouldAssertFalse() {
        assertFalse(O5DPatternStrategy.checkPattern(wrongPattern));
    }
}