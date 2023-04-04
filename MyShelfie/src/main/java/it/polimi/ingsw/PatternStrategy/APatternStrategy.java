package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

/**
 * a class implementing the Angular pattern strategy of the corresponding common goal card.
 */

public class APatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        final int maxr = shelfSnapshot.length;
        final int maxc = shelfSnapshot[0].length;
        if (shelfSnapshot[0][0] != Tile.BLANK && shelfSnapshot[0][0]!= Tile.NOT_VALID)
            return shelfSnapshot[0][0] == shelfSnapshot[0][maxc-1] && shelfSnapshot[0][0] == shelfSnapshot[maxr-1][0] && shelfSnapshot[0][0] == shelfSnapshot[maxr-1][maxc-1];
        else return false;
    }
}
