package it.polimi.ingsw;


import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the bag of all the drawable tiles of the game.
 */
public class Bag {
    private Map<Color,Integer> bag;
    private final static int MAX_TILES = 22;

    public Bag(){
        bag = new HashMap<>();
        for(int i = 0; i< Color.values().length;i++){
            bag.put(Color.values()[i],MAX_TILES);
        }
    }

    /**
     * This method allows to draw a single tile randomly chosen from the drawable remaining tiles of the game.
     * @return the single tile in order to be placed on the board.
     */
    public Color draw(){
        List<Color> remainingTiles = bag.keySet().stream().filter(x -> bag.get(x) > 0).collect(Collectors.toList());
        Random random = new Random();
        int i = random.nextInt(remainingTiles.size());

        Color drawableTile = remainingTiles.get(i);
        bag.put(drawableTile,bag.get(drawableTile)-1);
        return drawableTile;
    }
}