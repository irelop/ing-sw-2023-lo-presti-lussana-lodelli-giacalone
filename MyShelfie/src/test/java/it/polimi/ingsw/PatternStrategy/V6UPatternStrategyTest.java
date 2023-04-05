package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.ReadFileByLines;
import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class V6UPatternStrategyTest {
    StrategyInterface commonStrategy;
    Tile[][] correctPattern = null;
    Tile[][] wrongPattern = null;



    @BeforeEach
    void setUp() {
        commonStrategy = new V6UPatternStrategy();
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/v6uPS.txt");

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

    @Test
    void checkCorrectPattern_ShouldAssertTrue() {
        assertTrue(commonStrategy.checkPattern(correctPattern));
    }

    @Test
    void checkWrongPattern_ShouldAssertFalse() {
        assertFalse(commonStrategy.checkPattern(wrongPattern));
    }
}