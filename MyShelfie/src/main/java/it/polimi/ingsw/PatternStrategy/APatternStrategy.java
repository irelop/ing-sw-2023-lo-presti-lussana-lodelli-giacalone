package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

/**
 * a class implementing the Angular pattern strategy of the corresponding common goal card.
 */

public class APatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        final int maxr = shelfSnapshot.length-1;
        final int maxc = shelfSnapshot[0].length-1;
        return shelfSnapshot[0][0] == shelfSnapshot[0][maxc] && shelfSnapshot[0][0] == shelfSnapshot[maxr][0] && shelfSnapshot[0][0] == shelfSnapshot[maxr][maxc];
    }
}
