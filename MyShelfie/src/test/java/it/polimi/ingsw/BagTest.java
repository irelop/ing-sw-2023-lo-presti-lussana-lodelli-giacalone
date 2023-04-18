package it.polimi.ingsw;

import it.polimi.ingsw.Model.Exceptions.EmptyBagException;
import it.polimi.ingsw.Model.Bag;
import it.polimi.ingsw.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void singleDraw_shouldRemoveOneRandomTileFromBag() throws EmptyBagException {
        Tile randomTile = bag.draw();
        assertEquals(21,bag.getTileQuantity(randomTile));
    }
    @Test
    public void draw_shouldThrowEmptyBagException() throws EmptyBagException{
        for(int i = 0; i< (6*22) ; i++){
            this.bag.draw();
        }
        assertThrows(EmptyBagException.class,() -> this.bag.draw());
    }

    @Test
    public void draw_shouldNotThrowEmptyBagException() throws EmptyBagException{
        for(int i = 0; i< (6*22) -1;i++){
            bag.draw();
        }
        assertDoesNotThrow(()-> this.bag.draw());
    }
}