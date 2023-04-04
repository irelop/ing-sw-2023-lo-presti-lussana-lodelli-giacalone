package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.ReadFileByLines;
import it.polimi.ingsw.Shelf;
import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class V4UPatternStrategyTest {
    StrategyInterface v4uPatternStrategy = null;
    Tile[][] shelfSnapshot;

    @BeforeEach
    void setUp() {
        v4uPatternStrategy = new V4UPatternStrategy();
        shelfSnapshot = new Tile[6][5];

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/v4uPS_true.txt");

        for (int i = 0; i < 6; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                shelfSnapshot[i][j] = Tile.valueOf(values[j]);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void checkPattern_true() {
        assertTrue(v4uPatternStrategy.checkPattern(shelfSnapshot));
    }

    @Test
    void checkPattern_false() {
        shelfSnapshot[2][3] = Tile.GREEN;
        shelfSnapshot[3][3] = Tile.GREEN;
        shelfSnapshot[4][3] = Tile.GREEN;
        assertFalse(v4uPatternStrategy.checkPattern(shelfSnapshot));
    }
}