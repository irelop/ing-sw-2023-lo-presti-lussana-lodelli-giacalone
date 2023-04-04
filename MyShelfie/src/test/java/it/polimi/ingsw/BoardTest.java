package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    Board board;
    Tile[][] grid;

    ReadFileByLines reader;
    CommonGoalDeck deck = new CommonGoalDeck();


    @BeforeEach
    void setUp() {
        grid = new Tile[9][9];
        board = new Board();
        board.getBoardInstance();

        reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/board_3p.txt");

        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                grid[i][j] = Tile.valueOf(values[j]);
        }
        //set the grid in board with constructor from file
        board.initFromMatrix(grid);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void pickTilesFromBoard_correctEliminationOfTiles() {
        int inPosR = 1;
        int inPosC = 3;
        int numTiles = 2;
        char dir = 's';
        ArrayList<Tile> lista = new ArrayList<Tile>();
        lista = board.pickTilesFromBoard(inPosR,inPosC,numTiles,dir);
        grid[1][3] = Tile.BLANK;
        grid[2][3] = Tile.BLANK;
       // grid[3][3] = Tile.BLANK;

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                assertEquals(grid[i][j], board.getBoardGrid()[i][j]);


    }
}