package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

/**
 * a class implementing the - 8 cells of the same color - pattern strategy of the corresponding common goal card.
 */

public class M8PatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int cont = 0;
        final int maxr = shelfSnapshot.length-1;
        final int maxc = shelfSnapshot[0].length-1;
        for (Tile c : Tile.values()) {
            for (int i = 0; i <= maxr; i++) {
                for (int j = 0; j <= maxc; j++) {
                    if (shelfSnapshot[i][j] == c && c != Tile.BLANK)
                        cont++;
                    if (cont >= 8) {
                        return true;
                    }
                }
            }
            cont = 0;
        }
        return false;
    }
}