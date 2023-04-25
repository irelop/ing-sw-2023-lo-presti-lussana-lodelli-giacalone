package it.polimi.ingsw.Server.Model.PatternStrategy;

import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;

import java.util.Arrays;

/**
 * O5UPatternStrategy class: this class represents the strategy of the common goal card which checks if there are two rows
 *                           each one filled with 5 different types of tiles in the player's shelf.
 *
 * @author Andrea Giacalone
 */
public class O5UPatternStrategy implements StrategyInterface {

    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int counter = 0;
        int rows = shelfSnapshot.length;

        for(int i = 0; i< rows && counter < 2;i++){
            if(Arrays.stream(shelfSnapshot[i]).filter(x->x!=Tile.BLANK && x!= Tile.NOT_VALID).distinct().count()>=5) counter++;
        }

        return (counter ==2);
    }
}
