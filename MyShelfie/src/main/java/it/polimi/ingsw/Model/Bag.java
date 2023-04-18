package it.polimi.ingsw.Model;


import java.util.*;
import java.util.stream.Collectors;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Exceptions.EmptyBagException;

/**
 * Bag class: this class represents the bag of all the drawable tiles of the game.
 *
 * @author Andrea Giacalone
 */
public class Bag {
    private Map<Tile,Integer> bag;
    private final static int MAX_TILES = 22;

    public Bag(){
        bag = new HashMap<>();
        List<Tile> validTiles = Arrays.stream(Tile.values()).filter(x->x!=Tile.NOT_VALID && x!=Tile.BLANK).collect(Collectors.toList());
        for(Tile t: validTiles){
            bag.put(t,MAX_TILES);
        }
    }

    /**
     * OVERVIEW: this method allows to draw a single tile randomly chosen from the drawable remaining tiles of the game.
     * @return the single tile in order to be placed on the board.
     */
    public Tile draw() throws EmptyBagException {
        if (isBagEmpty()){
            throw new EmptyBagException();
        }
        else{
            List<Tile> remainingTiles = bag.keySet().stream().filter(x -> bag.get(x) > 0).toList();
            Random random = new Random();
            int i = random.nextInt(remainingTiles.size());

            Tile drawableTile = remainingTiles.get(i);
            bag.put(drawableTile, bag.get(drawableTile) - 1);
            return drawableTile;
        }
    }

    /**
     * OVERVIEW: this method allows to check if the game bag is already empty or not in order to refill the game board.
     * @return true: if it's empty.
     *         false: otherwise.
     */
    private boolean isBagEmpty(){
        return(this.bag.values().stream().filter(x->x!=0).count()==0);
    }

    public int getTileQuantity(Tile tile){
        return bag.get(tile);
    }
}