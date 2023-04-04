package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * a class implementing the - 8 cells of the same color - pattern strategy of the corresponding common goal card.
 */

public class M8PatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int cont = 0;
        final int maxr = shelfSnapshot.length;
        final int maxc = shelfSnapshot[0].length;

        //I use a list in order to avoid problem of casting types of tiles
        List<Tile> validTiles = Arrays.stream(Tile.values()).filter(x->x!=Tile.BLANK && x!=Tile.NOT_VALID).toList();
        for (Tile c : validTiles) {
            for (int i = 0; i < maxr; i++) {
                for (int j = 0; j < maxc; j++) {
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