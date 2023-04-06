package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.ReadFileByLines;
import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void checkCorrectPatternFromWest_ShouldAssertTrue() {
        assertTrue(commonStrategy.checkPattern(correctPatternWest));
    }

    @Test
    void checkCorrectPatternFromEast_ShouldAssertTrue() {
        assertTrue(commonStrategy.checkPattern(correctPatternEast));
    }

    @Test
    void checkWrongPattern_ShouldAssertFalse() {
        assertFalse(commonStrategy.checkPattern(wrongPattern));
    }
}