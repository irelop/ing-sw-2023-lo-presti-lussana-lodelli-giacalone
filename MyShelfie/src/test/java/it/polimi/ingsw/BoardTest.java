package it.polimi.ingsw;
/**
 * Tests for Board class
 * @authors Matteo Lussana, Irene Lo Presti
 */

import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.CommonGoalDeck;
import it.polimi.ingsw.Server.Model.Exceptions.*;
import it.polimi.ingsw.Server.Model.ReadFileByLines;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    Board board;
    Tile[][] grid;

    ReadFileByLines reader;
    CommonGoalDeck deck = new CommonGoalDeck();


    @BeforeEach
    void setUp() {
        grid = new Tile[9][9];
        board = new Board();
        Board.getBoardInstance();

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
        ArrayList<Tile> chosenTiles = new ArrayList<>();
        chosenTiles = board.pickTilesFromBoard(inPosR,inPosC,numTiles,dir);
        grid[1][3] = Tile.BLANK;
        grid[2][3] = Tile.BLANK;

        for(int i=0; i<9; i++)
           for(int j=0; j<9; j++)
                Assertions.assertEquals(grid[i][j], Board.getBoardGrid()[i][j]);

    }

    @Test
    void pickTilesFromBoard_correctTilesChosen() {
        int inPosR = 1;
        int inPosC = 3;
        int numTiles = 2;
        char dir = 's';
        ArrayList<Tile> chosenTiles = new ArrayList<>();
        ArrayList<Tile> correctTiles = new ArrayList<>();

        chosenTiles = board.pickTilesFromBoard(inPosR,inPosC,numTiles,dir);
        correctTiles.add(Tile.GREEN);
        correctTiles.add(Tile.GREEN);

        Assertions.assertEquals(chosenTiles, correctTiles);
    }

    @Test
    void getInitialRow_correctInputCorrectOutput() throws OutOfBoardException {
        assertEquals(0, board.getInitialRow(1));
    }

    @Test
    void getInitialRow_incorrectInputCorrectOutput() throws OutOfBoardException {
        assertThrows(OutOfBoardException.class, (Executable) ()->board.getInitialRow(13));
    }

    @Test
    void getInitialColumn_correctInputCorrectOutput() throws OutOfBoardException {
        assertEquals(0, board.getInitialColumn(1));
    }

    @Test
    void getInitialColumn_incorrectInputCorrectOutput(){
        assertThrows(OutOfBoardException.class, (Executable) ()->board.getInitialColumn(13));
    }
    
    @Test
    void checkPosition_cellNotValid(){
        assertThrows(InvalidCellException.class, (Executable) ()->board.checkPosition(0,0));
    }

    @Test
    void checkPosition_cellBlank(){
        assertThrows(EmptyCellException.class, (Executable) ()->board.checkPosition(3,6));
    }

    @Test
    void checkPosition_neighborFull(){
        assertThrows(InvalidPositionException.class, (Executable) ()->board.checkPosition(4,4));
    }

    @Test
    void getDirection_correctInputCorrectOutput() throws InvalidDirectionException {
        assertEquals('w', board.getDirection('w'));
    }
    @Test
    void getDirection_incorrectInputCorrectOutput(){
        assertThrows(InvalidDirectionException.class, (Executable) ()->board.getDirection('d'));
    }

    @Test
    void getNumberOfTiles_correctInputCorrectOutput() throws InvalidNumberOfTilesException {
        assertEquals(1, board.getNumberOfTiles(3,1));
    }

    @Test
    void getNumberOfTiles_incorrectInputCorrectOutput_tooManyTiles(){
        assertThrows(InvalidNumberOfTilesException.class, (Executable) ()->board.getNumberOfTiles(3,5));
    }

    @Test
    void checkDirectionAndNumberOfTiles_outOfBound(){
        //we add a tile only to control this exception
        grid[0][4] = Tile.YELLOW;
        board.initFromMatrix(grid);
        assertThrows(OutOfBoardException.class, (Executable) ()->board.checkDirectionAndNumberOfTiles('n', 3, 1, 4));
    }

    @Test
    void needRefill_false(){
        Assertions.assertFalse(board.needRefill());
    }

    @Test
    void needRefill_true(){
        //we need to empty the board in order to get no neighbour
        reader.readFrom("src/test/testFiles/board_3p_needRefill.txt");
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
        Assertions.assertTrue(board.needRefill());
    }

    @Test
    void refill_correctly(){
        //we need to empty the board in order to get no neighbour
        reader.readFrom("src/test/testFiles/board_3p_needRefill.txt");
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

        board.refill();

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                Assertions.assertNotEquals(Board.getBoardGrid()[i][j], Tile.BLANK);
    }
}