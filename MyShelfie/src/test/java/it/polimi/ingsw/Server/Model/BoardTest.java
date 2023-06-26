package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.utils.Exceptions.*;
import it.polimi.ingsw.utils.ReadFileByLines;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Board class
 * @author Matteo Lussana, Irene Lo Presti
 */
public class BoardTest {

    Board board;
    Tile[][] grid;
    ReadFileByLines reader;


    @BeforeEach
    void setUp() {
        grid = new Tile[9][9];
        board = new Board();

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

    /**
     * After choosing tiles from the board, the correspondent cells on the board should be empty (Tile.BLANK)
     */
    @Test
    void pickTilesFromBoard_correctEliminationOfTiles() {
        int inPosR = 1;
        int inPosC = 3;
        int numTiles = 2;
        char dir = 's';
        ArrayList<Tile> chosenTiles = board.pickTilesFromBoard(inPosR,inPosC,numTiles,dir);

        //remove manually the tiles from the grid
        grid[1][3] = Tile.BLANK;
        grid[2][3] = Tile.BLANK;

        for(int i=0; i<9; i++)
           for(int j=0; j<9; j++)
                Assertions.assertEquals(grid[i][j], board.getBoardGrid()[i][j]);
    }

    /**
     * After choosing tiles from the board, check if the method removes the correct ones
     */
    @Test
    void pickTilesFromBoard_correctTilesChosen() {
        int inPosR = 1;
        int inPosC = 3;
        int numTiles = 2;
        char dir = 's';
        //choose two green tiles
        ArrayList<Tile> chosenTiles = board.pickTilesFromBoard(inPosR,inPosC,numTiles,dir);

        ArrayList<Tile> correctTiles = new ArrayList<>();
        correctTiles.add(Tile.GREEN);
        correctTiles.add(Tile.GREEN);

        //check if from the board are removed two green tiles
        Assertions.assertEquals(correctTiles, chosenTiles);
    }

    /**
     * If the initial row is valid, check if the method doesn't throw the OutOfBoardException
     */
    @Test
    void getInitialRow_correctInputCorrectOutput() throws OutOfBoardException {
        assertEquals(0, board.getInitialRow(1));
    }

    /**
     * If the initial row is not valid (it is bigger than 9), check if the method throws the
     * OutOfBoardException
     */
    @Test
    void getInitialRow_RowTooHigh_CorrectOutput() {
        assertThrows(OutOfBoardException.class, ()->board.getInitialRow(13));
    }
    /**
     * If the initial row is not valid (it is less than 0), check if the method throws the
     * OutOfBoardException
     */
    @Test
    void getInitialRow_RowTooSmall_CorrectOutput() {
        assertThrows(OutOfBoardException.class, ()->board.getInitialRow(-2));
    }
    /**
     * If the initial column is valid, check if the method doesn't throw the OutOfBoardException
     */
    @Test
    void getInitialColumn_correctInputCorrectOutput() throws OutOfBoardException {
        assertEquals(0, board.getInitialColumn(1));
    }
    /**
     * If the initial column is not valid (it is bigger than 9), check if the method throws the
     * OutOfBoardException
     */
    @Test
    void getInitialColumn_ColumnTooHigh_CorrectOutput(){
        assertThrows(OutOfBoardException.class, ()->board.getInitialColumn(13));
    }
    /**
     * If the initial column is not valid (it is less than 0), check if the method throws the
     * OutOfBoardException
     */
    @Test
    void getInitialColumn_ColumnTooSmall_CorrectOutput(){
        assertThrows(OutOfBoardException.class, ()->board.getInitialColumn(-4));
    }

    /**
     * If the position chosen is not valid (it is on the board, but it is not a cell for
     * the tiles): Tile.NOT_VALID, check if throws the InvalidCellException
     */
    @Test
    void checkPosition_cellNotValid(){
        assertThrows(InvalidCellException.class, ()->board.checkPosition(0,0));
    }
    /**
     * If the position chosen is empty (it is on the board, it's a cell to be filled with tiles,
     * but the tile on the cell has already been chosen): Tile.BLANK, check if throws the
     * EmptyCellException
     */
    @Test
    void checkPosition_cellBlank(){
        assertThrows(EmptyCellException.class, ()->board.checkPosition(3,6));
    }

    /**
     * If the tile chosen hasn't got a free side (it has one tile to the north, one tile to the east,
     * one tile to the west and one tile to the west), check if throws the InvalidPositionException
     */
    @Test
    void checkPosition_neighborFull(){
        assertThrows(InvalidPositionException.class, ()->board.checkPosition(4,4));
    }

    /**
     * If the direction is valid (west), checks if the method doesn't throw the OutOfBoardException and
     * returns the inserted value
     */
    @Test
    void getDirection_InputWestCorrectOutput() throws InvalidDirectionException {
        assertEquals('w', board.getDirection('w'));
    }
    /**
     * If the direction is valid (east), checks if the method doesn't throw the OutOfBoardException and
     * returns the inserted value
     */
    @Test
    void getDirection_InputEastCorrectOutput() throws InvalidDirectionException {
        assertEquals('e', board.getDirection('e'));
    }
    /**
     * If the direction is valid (north), checks if the method doesn't throw the OutOfBoardException and
     * returns the inserted value
     */
    @Test
    void getDirection_InputNorthCorrectOutput() throws InvalidDirectionException {
        assertEquals('n', board.getDirection('n'));
    }
    /**
     * If the direction is valid (south), checks if the method doesn't throw the OutOfBoardException and
     * returns the inserted value
     */
    @Test
    void getDirection_InputSouthCorrectOutput() throws InvalidDirectionException {
        assertEquals('s', board.getDirection('s'));
    }

    /**
     * If the direction is not valid (it isn't a letter between 'e', 'w', 's', 'n'), check if
     * throws the InvalidDirectionException
     */
    @Test
    void getDirection_incorrectInputCorrectOutput(){
        assertThrows(InvalidDirectionException.class, ()->board.getDirection('d'));
    }

    /**
     * If the number of tiles is valid (number between 1 and maxTilesPickable (1-3)), checks
     * if the method doesn't throw the InvalidNumberOfTilesException and returns the inserted value
     */
    @Test
    void getNumberOfTiles_correctInputCorrectOutput() throws InvalidNumberOfTilesException {
        assertEquals(1, board.getNumberOfTiles(3,1));
    }

    /**
     * If the number of tiles is too high (bigger than the maxTilesPickable (1-3)), check if throws
     * the InvalidNumberOfTilesException
     */
    @Test
    void getNumberOfTiles_incorrectInputCorrectOutput_tooManyTiles(){
        assertThrows(InvalidNumberOfTilesException.class,
                ()->board.getNumberOfTiles(3,5));
    }

    /**
     * If the number of tiles is too low (negative or zero), check if throws
     * the InvalidNumberOfTilesException
     */
    @Test
    void getNumberOfTiles_incorrectInputCorrectOutput_tooFewTiles(){
        assertThrows(InvalidNumberOfTilesException.class, ()->board.getNumberOfTiles(3,0));
    }

    /**
     * If the second or the third tile chosen is out of the board, check if throws
     * the OutOfBoardException
     */
    @Test
    void checkDirectionAndNumberOfTiles_outOfBound(){
        //we add a tile only to control this exception
        grid[0][4] = Tile.YELLOW;
        board.initFromMatrix(grid);
        assertThrows(OutOfBoardException.class,
                ()->board.checkDirectionAndNumberOfTiles('n', 2, 1, 4,3));
    }

    /**
     * If the board is full, checks if the needRefill() method return false
     */
    @Test
    void needRefill_false(){
        Assertions.assertFalse(board.needRefill());
    }

    /**
     * If the board needs to be refilled, the needRefill() method should return true
     * The board needs to be refilled if is empty or if the tiles on it are all singles (without tiles adjacent to
     * them
     */
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

    /**
     * Check if the refill method refills the board correctly: leaving the tiles that were on the board and
     * filling the empty cells
     */
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
                Assertions.assertNotEquals(board.getBoardGrid()[i][j], Tile.BLANK);
    }

    /**
     * Test to check if the initGridParabolic for 2 players creates the correct board
     */
    @Test
    public void initGridParabolic2Players_correctCreation(){
        Tile[][] matrix = new Tile[9][9];
        reader.readFrom("src/test/testFiles/board_blank_2p.txt");
        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                matrix[i][j] = Tile.valueOf(values[j]);
        }

        Board boardVisual = new Board();
        boardVisual.initGridParabolic(2);

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                assertEquals(matrix[i][j], boardVisual.getBoardGrid()[i][j]);

    }

    /**
     * Test to check if the initGridParabolic for 3 players creates the correct board
     */
    @Test
    public void initGridParabolic3Players_correctCreation(){
        Tile[][] matrix = new Tile[9][9];
        reader.readFrom("src/test/testFiles/board_blank_3p.txt");
        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                matrix[i][j] = Tile.valueOf(values[j]);
        }

        Board boardVisual = new Board();
        boardVisual.initGridParabolic(3);

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                assertEquals(matrix[i][j], boardVisual.getBoardGrid()[i][j]);

    }

    /**
     * Test to check if the initGridParabolic for 4 players creates the correct board
     */
    @Test
    public void initGridParabolic4Players_correctCreation(){
        Tile[][] matrix = new Tile[9][9];
        reader.readFrom("src/test/testFiles/board_blank_4p.txt");
        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                matrix[i][j] = Tile.valueOf(values[j]);
        }

        Board boardVisual = new Board();
        boardVisual.initGridParabolic(4);

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                assertEquals(matrix[i][j], boardVisual.getBoardGrid()[i][j]);

    }

    /**
     * Test to check if the initGrid for 2 players creates the correct board
     */
    @Test
    public void initGrid2Players_correctCreation(){
        Tile[][] matrix = new Tile[9][9];
        reader.readFrom("src/test/testFiles/board_blank_2p.txt");
        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                matrix[i][j] = Tile.valueOf(values[j]);
        }

        Board boardVisual = new Board();
        boardVisual.initGrid(2);

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                assertEquals(matrix[i][j], boardVisual.getBoardGrid()[i][j]);


    }

    /**
     * Test to check if the initGrid for 3 players creates the correct board
     */
    @Test
    public void initGrid3Players_correctCreation(){
        Tile[][] matrix = new Tile[9][9];
        reader.readFrom("src/test/testFiles/board_blank_3p.txt");
        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                matrix[i][j] = Tile.valueOf(values[j]);
        }

        Board boardVisual = new Board();
        boardVisual.initGrid(3);

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                assertEquals(matrix[i][j], boardVisual.getBoardGrid()[i][j]);

    }

    /**
     * Test to check if the initGrid for 4 players creates the correct board
     */
    @Test
    public void initGrid4Players_correctCreation(){
        Tile[][] matrix = new Tile[9][9];
        reader.readFrom("src/test/testFiles/board_blank_4p.txt");
        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                matrix[i][j] = Tile.valueOf(values[j]);
        }

        Board boardVisual = new Board();
        boardVisual.initGrid(4);

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                assertEquals(matrix[i][j], boardVisual.getBoardGrid()[i][j]);

    }

}