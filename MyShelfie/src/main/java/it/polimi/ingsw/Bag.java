package it.polimi.ingsw;


import java.util.*;

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
        for(int i = 0; i< Tile.values().length; i++){
            bag.put(Tile.values()[i],MAX_TILES);
        }
    }

    /**
     * OVERVIEW: this method allows to draw a single tile randomly chosen from the drawable remaining tiles of the game.
     * @return the single tile in order to be placed on the board.
     */
    public Tile draw(){
        List<Tile> remainingTiles = bag.keySet().stream().filter(x -> bag.get(x) > 0).toList();
        Random random = new Random();
        int i = random.nextInt(remainingTiles.size());

        Tile drawableTile = remainingTiles.get(i);
        bag.put(drawableTile,bag.get(drawableTile)-1);
        return drawableTile;
    }
}