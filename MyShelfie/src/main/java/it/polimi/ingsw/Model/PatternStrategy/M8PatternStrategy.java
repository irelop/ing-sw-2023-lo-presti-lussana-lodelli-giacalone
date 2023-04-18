package it.polimi.ingsw.Model.PatternStrategy;

import it.polimi.ingsw.Model.StrategyInterface;
import it.polimi.ingsw.Model.Tile;

import java.util.Arrays;
import java.util.List;

/**
 * M8PatternStrategy class: this class represents the strategy of the common goal card which checks if there are 8 tiles
 *                          of the same type in the player's shelf, without any restrictions on the position of these ones.
 *
 * @author Riccardo Lodelli
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
                    if (shelfSnapshot[i][j] == c)
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