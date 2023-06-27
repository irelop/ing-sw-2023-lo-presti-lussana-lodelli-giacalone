package it.polimi.ingsw.Server.Model;
import it.polimi.ingsw.Server.controller.MyShelfie;
import it.polimi.ingsw.utils.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.utils.Exceptions.OutOfBoardException;
import it.polimi.ingsw.utils.ReadFileByLines;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Player class
 * @author Irene Lo Presti, Matteo Lussana
 */
public class PlayerTest {
    Player player;

    @Before
    public void setUp() throws Exception {
        player = new Player("player1");
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test for orderTiles() method (1 tile): ensures that the only tile
     * in littleHand isn't modified in any way
     */
    @Test
    public void orderTiles_singleTile() {
        ArrayList<Tile> chosenTiles = new ArrayList<>();

        chosenTiles.add(Tile.BLUE);
        player.setLittleHand(chosenTiles);

        int[] order = new int[] {0};
        player.orderTiles(order);
        assertEquals(chosenTiles, player.getLittleHand());
    }

    /**
     * Test for orderTiles() method (2 tile): ensures that, given an order for the 2 tiles,
     * player's littleHand is correctly reordered after the invocation of the method
     */
    @Test
    public void orderTiles_twoTiles() {
        ArrayList<Tile> chosenTiles = new ArrayList<>();
        chosenTiles.add(Tile.BLUE);
        chosenTiles.add(Tile.GREEN);

        player.setLittleHand(chosenTiles);

        //set the array of the player's choices
        int[] order = new int[] {1, 0};
        player.orderTiles(order);

        //change order of chooseTiles manually
        chosenTiles.remove(0);
        chosenTiles.add(Tile.BLUE);
        assertEquals(chosenTiles, player.getLittleHand());
    }

    /**
     * Test for orderTiles() method (3 tile): ensures that, given an order for the 3 tiles,
     * player's littleHand is correctly reordered after the invocation of the method
     */
    @Test
    public void orderTiles_threeTiles() {
        ArrayList<Tile> chosenTiles = new ArrayList<>();
        chosenTiles.add(Tile.BLUE);
        chosenTiles.add(Tile.GREEN);
        chosenTiles.add(Tile.YELLOW);

        player.setLittleHand(chosenTiles);

        //set the array of the player's choices
        int[] order = new int[] {1, 2 ,0};
        player.orderTiles(order);

        //change order of chooseTiles manually
        chosenTiles.remove(0);
        chosenTiles.add(Tile.BLUE);
        assertEquals(chosenTiles, player.getLittleHand());
    }

    /**
     * Test for clearLittleHand() method : ensures that, fulfilling littleHand,
     * it is correctly cleared after the invocation of the method
     */
    @Test
    public void clearLittleHand(){
        ArrayList<Tile> chosenTiles = new ArrayList<>();
        chosenTiles.add(Tile.BLUE);
        chosenTiles.add(Tile.PINK);
        chosenTiles.add(Tile.WHITE);
        player.setLittleHand(chosenTiles);

        player.clearLittleHand();
        assert(player.getLittleHand().size() == 0);
    }

    /**
     * Test for getTiles(choices) method: ensures the correct throw of
     * InvalidTileIndexInLittleHandException, when user inserts 2 times the same index
     */
    @Test
    public void getTiles_duplicateChoice(){
        int[] choices = new int[] {1, 2 ,2};
        assertThrows(InvalidTileIndexInLittleHandException.class, ()->player.getTiles(choices));
    }

    /**
     * Test for getTiles(choices) method: ensures the correct throw of
     * InvalidTileIndexInLittleHandException, when user inserts a negative number as an index
     */
    @Test
    public void getTiles_negativeChoice(){
        int[] choices = new int[] {1, 2 ,-2};
        assertThrows(InvalidTileIndexInLittleHandException.class, ()->player.getTiles(choices));
    }

    /**
     * Test for getTiles(choices) method: ensures the correct throw of
     * InvalidTileIndexInLittleHandException, when user inserts an index > 2
     */
    @Test
    public void getTiles_choiceTooHigh(){
        int[] choices = new int[] {1, 2 ,3};
        assertThrows(InvalidTileIndexInLittleHandException.class, ()->player.getTiles(choices));
    }

    /**
     * Test for addScore(score) method: ensures the player gains the
     * correct amount of points when the method is invoked
     */
    @Test
    public void addScore(){
        player.addScore(3);
        assertEquals(player.getScore(), 3);
    }

    /**
     * Test for setCommonGoalAchieved(commonCardIdx) method: ensures player can't
     * achieve the same card more than once (setting to false a boolean variable)
     */
    @Test
    public void setCommonGoalAchieved(){
        player.setCommonGoalAchieved(0);
        assertTrue(player.isCommonGoalAchieved(0));
        assertFalse(player.isCommonGoalAchieved(1));
    }

    /**
     * Test for setShelf(newShelf) method: ensures the setter works properly
     */
    @Test
    public void setShelf(){
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/ShelfTest.txt");

        Tile[][] grid = new Tile[6][5];
        for (int i = 0; i < 6; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                grid[i][j] = Tile.valueOf(values[j]);
        }

        player.setShelf(grid);
        assertEquals(player.getShelfGrid(), grid);
    }
}