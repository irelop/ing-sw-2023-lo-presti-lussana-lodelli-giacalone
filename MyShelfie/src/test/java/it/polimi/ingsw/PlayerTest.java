package it.polimi.ingsw;

        import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;

        import java.io.*;
        import java.util.ArrayList;

        import static org.junit.Assert.*;

public class PlayerTest {
    Player player;

    @Before
    public void setUp() throws Exception {
        player = new Player("player1");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void orderTiles_singleTile() {
        ArrayList<Tile> choosenTiles = new ArrayList<>();
        choosenTiles.add(Tile.BLUE);
        int[] order = new int[] {1};
        player.orderTiles(choosenTiles,order);
        assertEquals(choosenTiles, player.getLittleHand());
    }

    @Test
    public void orderTiles_twoTiles() {
        ArrayList<Tile> choosenTiles = new ArrayList<>();
        choosenTiles.add(Tile.BLUE);
        choosenTiles.add(Tile.GREEN);
        //set the array of the player's choices
        int[] order = new int[] {2, 1};
        player.orderTiles(choosenTiles, order);

        //change order of chooseTiles manually
        choosenTiles.remove(0);
        choosenTiles.add(Tile.BLUE);
        assertEquals(choosenTiles, player.getLittleHand());
    }

    @Test
    public void orderTiles_threeTiles() {
        ArrayList<Tile> choosenTiles = new ArrayList<>();
        choosenTiles.add(Tile.BLUE);
        choosenTiles.add(Tile.GREEN);
        choosenTiles.add(Tile.YELLOW);
        //set the array of the player's choices
        int[] order = new int[] {3, 1 ,2};
        player.orderTiles(choosenTiles, order);

        //change order of chooseTiles manually
        choosenTiles.remove(0);
        choosenTiles.add(Tile.BLUE);
        assertEquals(choosenTiles, player.getLittleHand());
    }
}