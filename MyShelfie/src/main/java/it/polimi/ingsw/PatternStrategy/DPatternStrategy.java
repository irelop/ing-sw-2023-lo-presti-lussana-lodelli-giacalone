package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

/**
 * a class implementing the X pattern strategy of the corresponding common goal card.
 */

public class DPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int numRow = shelfSnapshot.length;
        int numCol = shelfSnapshot[0].length;

        for (int i = 0; i < numRow - 4; i++)
            for (int j = 0; j < numCol - 4; j++) {
                if (shelfSnapshot[i][j] == shelfSnapshot[i + 1][j + 1] &&
                    shelfSnapshot[i][j] == shelfSnapshot[i + 2][j + 2] &&
                    shelfSnapshot[i][j] == shelfSnapshot[i + 3][j + 3] &&
                    shelfSnapshot[i][j] == shelfSnapshot[i + 4][j + 4]) return true;

                if (shelfSnapshot[numRow-1-i][numCol-1-j] == shelfSnapshot[numRow-1-i-1][numCol-1-j-1] &&
                    shelfSnapshot[numRow-1-i][numCol-1-j] == shelfSnapshot[numRow-1-i-2][numCol-1-j-2] &&
                    shelfSnapshot[numRow-1-i][numCol-1-j] == shelfSnapshot[numRow-1-i-3][numCol-1-j-3] &&
                    shelfSnapshot[numRow-1-i][numCol-1-j] == shelfSnapshot[numRow-1-i-4][numCol-1-j-4] ) return true;

            }
        return false;
    }
}
