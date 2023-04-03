package it.polimi.ingsw;

        import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;

        import java.io.ByteArrayInputStream;
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
        player.orderTiles(choosenTiles);
        assertEquals(choosenTiles, player.getLittleHand());
    }

    @Test
    public void orderTiles_twoTiles() {
        ArrayList<Tile> choosenTiles = new ArrayList<>();
        choosenTiles.add(Tile.BLUE);
        choosenTiles.add(Tile.GREEN);
        //set the stream of input in order to change the order in the function
        String input1 = "2";
        String input2 = "1";
        String simulatedInput = input1 + System.getProperty("line.separator")
                + input2 + System.getProperty("line.separator");

        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        player.orderTiles(choosenTiles);
        //change order of chooseTiles manually
        choosenTiles.remove(1);
        choosenTiles.add(Tile.BLUE);
        assertEquals(choosenTiles, player.getLittleHand());
    }

}