package it.polimi.ingsw;
/**
 * Tests for Board class
 * @authors Matteo Lussana, Irene Lo Presti
 */

import it.polimi.ingsw.Server.Model.*;
import it.polimi.ingsw.Server.Model.Exceptions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


import java.util.ArrayList;

import static it.polimi.ingsw.Client.View.ColorCode.*;
import static it.polimi.ingsw.Client.View.ColorCode.RESET;
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
        board.pickTilesFromBoard(inPosR,inPosC,numTiles,dir,new Player("player1"));
        grid[1][3] = Tile.BLANK;
        grid[2][3] = Tile.BLANK;

        for(int i=0; i<9; i++)
           for(int j=0; j<9; j++)
                Assertions.assertEquals(grid[i][j], board.getBoardGrid()[i][j]);

    }

    @Test
    void pickTilesFromBoard_correctTilesChosen() {
        int inPosR = 1;
        int inPosC = 3;
        int numTiles = 2;
        char dir = 's';
        Player player = new Player("player1");
        ArrayList<Tile> correctTiles = new ArrayList<>();

        board.pickTilesFromBoard(inPosR,inPosC,numTiles,dir,player);


        correctTiles.add(Tile.GREEN);
        correctTiles.add(Tile.GREEN);

        Assertions.assertEquals(player.getLittleHand(), correctTiles);
    }

    @Test
    void getInitialRow_correctInputCorrectOutput() throws OutOfBoardException {
        assertEquals(0, board.getInitialRow(1));
    }

    @Test
    void getInitialRow_incorrectInputCorrectOutput() {
        assertThrows(OutOfBoardException.class, ()->board.getInitialRow(13));
    }

    @Test
    void getInitialColumn_correctInputCorrectOutput() throws OutOfBoardException {
        assertEquals(0, board.getInitialColumn(1));
    }

    @Test
    void getInitialColumn_incorrectInputCorrectOutput(){
        assertThrows(OutOfBoardException.class, ()->board.getInitialColumn(13));
    }
    
    @Test
    void checkPosition_cellNotValid(){
        assertThrows(InvalidCellException.class, ()->board.checkPosition(0,0));
    }

    @Test
    void checkPosition_cellBlank(){
        assertThrows(EmptyCellException.class, ()->board.checkPosition(3,6));
    }

    @Test
    void checkPosition_neighborFull(){
        assertThrows(InvalidPositionException.class, ()->board.checkPosition(4,4));
    }

    @Test
    void getDirection_correctInputCorrectOutput() throws InvalidDirectionException {
        assertEquals('w', board.getDirection('w'));
    }
    @Test
    void getDirection_incorrectInputCorrectOutput(){
        assertThrows(InvalidDirectionException.class, ()->board.getDirection('d'));
    }

    @Test
    void getNumberOfTiles_correctInputCorrectOutput() throws InvalidNumberOfTilesException {
        assertEquals(1, board.getNumberOfTiles(3,1));
    }

    @Test
    void getNumberOfTiles_incorrectInputCorrectOutput_tooManyTiles(){
        assertThrows(InvalidNumberOfTilesException.class, ()->board.getNumberOfTiles(3,5));
    }

    @Test
    void checkDirectionAndNumberOfTiles_outOfBound(){
        //we add a tile only to control this exception
        grid[0][4] = Tile.YELLOW;
        board.initFromMatrix(grid);
        assertThrows(OutOfBoardException.class, ()->board.checkDirectionAndNumberOfTiles('n', 3, 1, 4,3));
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
                Assertions.assertNotEquals(board.getBoardGrid()[i][j], Tile.BLANK);
    }

    @Test
    public void initGrid2Players_visualTest(){
        Board boardVisual = new Board();
        boardVisual.initGrid(2);

        String code = "\u25CF";
        for(int r=0; r<9; r++){
            for(int c=0; c<9; c++){

                switch (boardVisual.getBoardGrid()[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(BLANK.code + code + RESET.code);
                    case PINK -> System.out.print(PINK.code + code + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + code + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + code + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + code + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.println();
        }

    }

    @Test
    public void initGrid3Players_visualTest(){
        Board boardVisual = new Board();
        boardVisual.initGrid(3);

        String code = "\u25CF";
        for(int r=0; r<9; r++){
            for(int c=0; c<9; c++){

                switch (boardVisual.getBoardGrid()[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(BLANK.code + code + RESET.code);
                    case PINK -> System.out.print(PINK.code + code + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + code + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + code + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + code + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.println();
        }

    }

    @Test
    public void initGrid4Players_visualTest(){
        Board boardVisual = new Board();
        boardVisual.initGrid(4);

        String code = "\u25CF";
        for(int r=0; r<9; r++){
            for(int c=0; c<9; c++){

                switch (boardVisual.getBoardGrid()[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(BLANK.code + code + RESET.code);
                    case PINK -> System.out.print(PINK.code + code + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + code + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + code + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + code + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.println();
        }

    }

    @Test
    public void initGridParabolic_visualTest(){
        Board boardVisual = new Board();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boardVisual.getBoardGrid()[i][j] = null;
            }
        }
        boardVisual.getBoardGrid()[9 / 2][9 / 2] = Tile.BLANK;

        boardVisual.initGridParabolic(2);


    }


}