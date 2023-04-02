package it.polimi.ingsw;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class ShelfTest {
    Shelf shelf;
    Tile[][] grid;
    int [][] beenThere;

    @Before
    public void setUp(){


        grid = new Tile[6][5];

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/ShelfTest.txt");

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

    @Test
    public void insert() {
    }

    @Test
    public void spotCheck_correctInput_correctOutput() {
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/ShelfTest_spotCheck.txt");

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
    @Test
    public void maxTilesPickable_corretInput_wrongOutput_false() {
        assertNotEquals(1,shelf.maxTilesPickable());
        assertNotEquals(2,shelf.maxTilesPickable());
        assertNotEquals(4,shelf.maxTilesPickable());
    }
}