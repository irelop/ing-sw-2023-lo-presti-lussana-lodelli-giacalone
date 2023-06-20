package it.polimi.ingsw.Server.Model;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        ArrayList<Tile> chosenTiles = new ArrayList<>();

        chosenTiles.add(Tile.BLUE);
        player.setLittleHand(chosenTiles);

        int[] order = new int[] {1};
        player.orderTiles(order);
        assertEquals(chosenTiles, player.getLittleHand());
    }

    @Test
    public void orderTiles_twoTiles() {
        ArrayList<Tile> chosenTiles = new ArrayList<>();
        chosenTiles.add(Tile.BLUE);
        chosenTiles.add(Tile.GREEN);

        player.setLittleHand(chosenTiles);

        //set the array of the player's choices
        int[] order = new int[] {2, 1};
        player.orderTiles(order);

        //change order of chooseTiles manually
        chosenTiles.remove(0);
        chosenTiles.add(Tile.BLUE);
        assertEquals(chosenTiles, player.getLittleHand());
    }

    @Test
    public void orderTiles_threeTiles() {
        ArrayList<Tile> chosenTiles = new ArrayList<>();
        chosenTiles.add(Tile.BLUE);
        chosenTiles.add(Tile.GREEN);
        chosenTiles.add(Tile.YELLOW);

        player.setLittleHand(chosenTiles);

        //set the array of the player's choices
        int[] order = new int[] {3, 1 ,2};
        player.orderTiles(order);

        //change order of chooseTiles manually
        chosenTiles.remove(0);
        chosenTiles.add(Tile.BLUE);
        assertEquals(chosenTiles, player.getLittleHand());
    }
}