package it.polimi.ingsw.Server.Model.PatternStrategy;

import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;

/**
 * APatternStrategy class: this class represents the strategy of the common goal card which checks if the 4 corners of
 *                         the player's shelf are filled with a tile of the same type.
 *
 * @author Riccardo Lodelli
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
