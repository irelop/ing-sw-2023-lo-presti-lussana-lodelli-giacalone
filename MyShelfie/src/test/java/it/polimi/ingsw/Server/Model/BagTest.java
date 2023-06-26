package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.utils.Exceptions.EmptyBagException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Bag class
 * @author Andrea Giacalone
 */
public class BagTest {
    private Bag bag;

    @BeforeEach
    void setUp() {
        this.bag = new Bag();
    }

    @AfterEach
    void tearDown() {
        this.bag = null;
    }

    /**
     * Check if the bag updates correctly: after drawing one tile, check if in the bag there is one less
     * tile of the random type extracted
     */
    @Test
    public void singleDraw_shouldRemoveOneRandomTileFromBag() throws EmptyBagException {
        Tile randomTile = bag.draw();
        assertEquals(21,bag.getTileQuantity(randomTile));
    }

    /**
     * After removing all the tiles from the bag, this test checks if the method draw throws the
     * EmptyBagException when it's called.
     */
    @Test
    public void draw_shouldThrowEmptyBagException() throws EmptyBagException{
        for(int i = 0; i< (6*22) ; i++){
            this.bag.draw();
        }
        assertThrows(EmptyBagException.class,() -> this.bag.draw());
    }

    /**
     * This test checks if the draw method doesn't throw the EmptyBagException after calling it for the
     * second time (the bag is not empty)
     */
    @Test
    public void draw_shouldNotThrowEmptyBagException() throws EmptyBagException{
        for(int i = 0; i< (6*22) -1;i++){
            bag.draw();
        }
        assertDoesNotThrow(()-> this.bag.draw());
    }
}