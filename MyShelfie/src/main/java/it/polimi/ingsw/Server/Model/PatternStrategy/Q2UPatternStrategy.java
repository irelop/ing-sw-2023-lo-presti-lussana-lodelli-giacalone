package it.polimi.ingsw.Server.Model.PatternStrategy;

import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;

/**
 * Q2UPatternStrategy class: this class represents the strategy of the common goal card which checks if there are two
 *                           separated groups of 4 tiles, forming a 2x2 square with the restriction that both groups
 *                           must have the same type of tiles.
 *
 * @author Andrea Giacalone
 */

public class Q2UPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;
        Tile[][] copy = new Tile[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                copy[i][j] = shelfSnapshot[i][j];
            }
        }
        Tile foundTile = Tile.BLANK;


        for(int i=0;i<rows-1;i++) {
            for (int j = 0; j < columns - 1; j++) {
                if (foundTile == Tile.BLANK) {
                    if (copy[i][j] != Tile.BLANK && copy[i][j] != Tile.NOT_VALID) {
                        if (copy[i][j] == copy[i][j + 1] &&
                                copy[i][j] == copy[i + 1][j] &&
                                copy[i][j] == copy[i + 1][j + 1]) {
                            foundTile = copy[i][j];
                            copy[i][j] = Tile.NOT_VALID;
                            copy[i][j + 1] = Tile.NOT_VALID;
                            copy[i + 1][j] = Tile.NOT_VALID;
                            copy[i + 1][j + 1] = Tile.NOT_VALID;
                        }
                    }
                } else if(foundTile != Tile.NOT_VALID){
                    if (copy[i][j] != Tile.BLANK && copy[i][j] != Tile.NOT_VALID) {
                        if (copy[i][j] == foundTile &&
                                copy[i][j] == copy[i][j + 1] &&
                                copy[i][j] == copy[i + 1][j] &&
                                copy[i][j] == copy[i + 1][j + 1]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
