package it.polimi.ingsw.Server.Model.PatternStrategy;

import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;

/**
 * DPatternStrategy class: this class represents the strategy of the common goal card which checks if one of the major
 *                         diagonal of the player's shelf are filled with tiles of the same type.
 *
 * @author Matteo Lussana
 */

public class DPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        final int numRow = shelfSnapshot.length; //6
        final int numCol = shelfSnapshot[0].length; //5

        for (int i = 0; i < numRow - 4; i++) {
            for (int j = 0; j < numCol - 4; j++) {
                if(shelfSnapshot[i][j]!= Tile.BLANK && shelfSnapshot[i][j]!= Tile.NOT_VALID) {
                    if (shelfSnapshot[i][j] == shelfSnapshot[i + 1][j + 1] &&
                            shelfSnapshot[i][j] == shelfSnapshot[i + 2][j + 2] &&
                            shelfSnapshot[i][j] == shelfSnapshot[i + 3][j + 3] &&
                            shelfSnapshot[i][j] == shelfSnapshot[i + 4][j + 4])
                        return true;
                }

                if (shelfSnapshot[i][numCol-1-j] != Tile.BLANK && shelfSnapshot[i][numCol-1-j] != Tile.NOT_VALID) {
                    if (shelfSnapshot[i][numCol-1-j] == shelfSnapshot[i+1][numCol-2-j] &&
                            shelfSnapshot[i][numCol-1-j] == shelfSnapshot[i+2][numCol-3-j] &&
                            shelfSnapshot[i][numCol-1-j] == shelfSnapshot[i+3][numCol-4-j] &&
                            shelfSnapshot[i][numCol-1-j] == shelfSnapshot[i+4][numCol-5-j])
                        return true;
                }
            }
        }
        return false;
    }
}
