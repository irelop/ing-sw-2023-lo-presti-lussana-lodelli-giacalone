package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.utils.Exceptions.NotEnoughSpaceInChosenColumnException;
        import it.polimi.ingsw.utils.ReadFileByLines;
        import it.polimi.ingsw.Server.Model.Shelf;
        import it.polimi.ingsw.Server.Model.Tile;
        import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;

        import java.util.ArrayList;

        import static org.junit.Assert.*;

/**
 * Tests for Shelf class
 * @author Matteo Lussana
 */
public class ShelfTest {
    Shelf shelf;
    Tile[][] grid;
    int [][] beenThere;

    @Before
    public void setUp(){


        grid = new Tile[6][5];

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/ShelfTest.txt");

        for (int i = 0; i < 6; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                grid[i][j] = Tile.valueOf(values[j]);
        }
        shelf = new Shelf(grid);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test for insert(columnIdx,tilesToInsert) method that checks if
     * a pre-built shelf is the same as a shelf in which tiles are being inserted
     * @throws NotEnoughSpaceInChosenColumnException: thrown when the column chosen is full
     */
    @Test
    public void insert_correctInput_correctOutput() throws NotEnoughSpaceInChosenColumnException {
        ArrayList<Tile> littleHand = new ArrayList<Tile>();
        littleHand.add(Tile.WHITE);
        littleHand.add(Tile.BLUE);
        littleHand.add(Tile.GREEN);
        shelf.insert(0, littleHand);

        grid[0][3] = Tile.GREEN;
        grid[0][2] = Tile.GREEN;
        grid[0][1] = Tile.GREEN;
        Shelf shelf2 = new Shelf(grid);

        for(int r=0; r<6; r++){
            for(int c=0; c<5; c++)
                assertEquals(shelf2.getGrid()[r][c], shelf.getGrid()[r][c]);
        }
    }

    /**
     * Test for insert(columnIdx,tilesToInsert) method that checks if
     * NotEnoughSpaceInChosenColumnException is correctly thrown
     * @throws NotEnoughSpaceInChosenColumnException: thrown when the column chosen is full
     */
    @Test(expected = NotEnoughSpaceInChosenColumnException.class)
    public void insert_exception() throws NotEnoughSpaceInChosenColumnException{
        grid[3][0] = Tile.GREEN;
        grid[2][0] = Tile.GREEN;
        grid[1][0] = Tile.GREEN;
        Shelf shelf = new Shelf(grid);

        ArrayList<Tile> littleHand = new ArrayList<Tile>();

        littleHand.add(Tile.WHITE);
        littleHand.add(Tile.BLUE);
        littleHand.add(Tile.GREEN);

        shelf.insert(0, littleHand);

    }

    /**
     * Test for spotCheck() method: having an amount of adjacent tiles of the same color
     * brings the player some point. Here there is the check of that amount of points
     */
    @Test
    public void spotCheck_correctInput_correctOutput() {
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/ShelfTest_spotCheck.txt");

        for (int i = 0; i < 6; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                grid[i][j] = Tile.valueOf(values[j]);
        }
        shelf = new Shelf(grid);

        assertEquals(15,shelf.spotCheck());
    }

    /**
     * Test for isShelfFull() method: this test checks a shelf either
     * full or not full, and ensures the method returns true and false
     */
    @Test
    public void isShelfFull_correctInput_correctOutput() {
        for(int r=0; r<6; r++)
            for(int c=0; c<5; c++)
                grid[r][c] = Tile.BLUE;
        shelf = new Shelf(grid);
        assertTrue(shelf.isShelfFull());

        grid[0][4] = Tile.BLANK;
        shelf = new Shelf(grid);
        assertFalse(shelf.isShelfFull());
    }

    /**
     * Test for maxTilesPickable() method: checks how many tiles can player draw
     * from the board, based on the maximum space that the player's shelf has in its columns
     * (can be 0, 1, 2 or 3 tiles)
     */
    @Test
    public void maxTilesPickable_corretInput_correctOutput_true() {
        //top four taws empty
        shelf = new Shelf(grid);
        assertEquals(3,shelf.maxTilesPickable());

        //filling the fourth (from top)
        for(int i = 0; i<5 ; i++)
            grid[3][i] = Tile.BLUE;
        shelf = new Shelf(grid);
        assertEquals(3,shelf.maxTilesPickable());

        //filling the third (from top)
        for(int i = 0; i<5 ; i++)
            grid[2][i] = Tile.BLUE;
        shelf = new Shelf(grid);
        assertEquals(2,shelf.maxTilesPickable());

        //filling the second (from top)
        for(int i = 0; i<5 ; i++)
            grid[1][i] = Tile.BLUE;
        shelf = new Shelf(grid);
        assertEquals(1,shelf.maxTilesPickable());

        //filling the first (from top)
        for(int i = 0; i<5 ; i++)
            grid[0][i] = Tile.BLUE;
        shelf = new Shelf(grid);
        assertEquals(0,shelf.maxTilesPickable());

        //creating a hole
        grid[0][3] = Tile.BLANK;
        grid[0][2] = Tile.BLANK;
        shelf = new Shelf(grid);
        assertEquals(1,shelf.maxTilesPickable());

        grid[1][3] = Tile.BLANK;
        shelf = new Shelf(grid);
        assertEquals(2,shelf.maxTilesPickable());

        grid[2][3] = Tile.BLANK;
        grid[1][2] = Tile.BLANK;
        shelf = new Shelf(grid);
        assertEquals(3,shelf.maxTilesPickable());


    }

    /**
     * Test for maxTilesPickable() method: checks how many tiles can player draw
     * from the board, based on the maximum space that the player's shelf has in its columns
     * (can be 0, 1, 2 or 3 tiles). This test works in an opposite way: it ensures
     * returned values are not equals to wrong values
     */
    @Test
    public void maxTilesPickable_corretInput_wrongOutput_false() {
        assertNotEquals(1,shelf.maxTilesPickable());
        assertNotEquals(2,shelf.maxTilesPickable());
        assertNotEquals(4,shelf.maxTilesPickable());
    }
}