package it.polimi.ingsw;

        import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;

        import java.io.*;
        import java.util.ArrayList;
        import java.util.Scanner;

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
        player.orderTiles(choosenTiles);
        assertEquals(choosenTiles, player.getLittleHand());
    }

    @Test
    public void orderTiles_twoTiles() {
        ArrayList<Tile> choosenTiles = new ArrayList<>();
        choosenTiles.add(Tile.BLUE);
        choosenTiles.add(Tile.GREEN);
        //set the stream of input in order to change the order in the function



        player.orderTiles(choosenTiles);

        //change order of chooseTiles manually
        choosenTiles.remove(1);
        choosenTiles.add(Tile.BLUE);
        assertEquals(choosenTiles, player.getLittleHand());
    }

}