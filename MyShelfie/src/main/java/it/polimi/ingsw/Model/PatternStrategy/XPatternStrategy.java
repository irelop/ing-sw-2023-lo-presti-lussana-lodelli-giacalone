package it.polimi.ingsw.Model.PatternStrategy;

import it.polimi.ingsw.Model.StrategyInterface;
import it.polimi.ingsw.Model.Tile;

/**
 * XPatternStrategy class: this class represents the strategy of the common goal card which checks if there are 5 tiles
 *                         of the same type forming an X shape in the player's shelf.
 *
 * @author Andrea Giacalone
 */
public class XPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int numRow = shelfSnapshot.length;
        int numCol = shelfSnapshot[0].length;

        for(int i=0; i<numRow - 2; i++){
            for(int j=0; j<numCol - 2;j++){
               if(shelfSnapshot[i][j] != Tile.BLANK &&
                       shelfSnapshot[i][j] != Tile.NOT_VALID &&
                       shelfSnapshot[i][j]==shelfSnapshot[i][j+2] &&
                       shelfSnapshot[i][j]==shelfSnapshot[i+1][j+1] &&
                       shelfSnapshot[i][j]==shelfSnapshot[i+2][j] &&
                       shelfSnapshot[i][j]==shelfSnapshot[i+2][j+2])
                   return true;
            }
        }
        return false;
    }
}
