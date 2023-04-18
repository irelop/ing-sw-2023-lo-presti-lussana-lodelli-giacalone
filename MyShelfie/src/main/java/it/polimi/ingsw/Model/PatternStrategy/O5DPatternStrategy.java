package it.polimi.ingsw.Model.PatternStrategy;

import it.polimi.ingsw.Model.StrategyInterface;
import it.polimi.ingsw.Model.Tile;

import java.util.Arrays;

/**
 * O5DPatternStrategy class: this class represents the strategy of the common goal card which checks if there are 4 rows
 *                           each one filled by tiles of max 3 different type of tiles. Each row can have a random
 *                           combination of different types of tiles.
 *
 * @author Andrea Giacalone
 */
public class O5DPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int counter = 0;
        int rows = shelfSnapshot.length;

        for (int i = 0; i < rows && counter < 4; i++) {
            if (Arrays.stream(shelfSnapshot[i]).filter(x -> x != Tile.BLANK).count() == 5) {
                if (Arrays.stream(shelfSnapshot[i]).filter(x -> x != Tile.BLANK && x!=Tile.NOT_VALID).distinct().count() <= 3) counter++;
            }
        }

        return (counter == 4);
    }
}
