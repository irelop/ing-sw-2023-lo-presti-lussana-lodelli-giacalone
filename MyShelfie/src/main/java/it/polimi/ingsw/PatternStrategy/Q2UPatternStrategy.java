package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;


public class Q2UPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;
        Tile foundTile = Tile.BLANK;


        for(int i=0;i<rows-1;i++) {
            for (int j = 0; j < columns - 1; j++) {
                if (foundTile == Tile.BLANK) {
                    if (shelfSnapshot[i][j] != Tile.BLANK && shelfSnapshot[i][j] != Tile.NOT_VALID) {
                        if (shelfSnapshot[i][j] == shelfSnapshot[i][j + 1] &&
                                shelfSnapshot[i][j] == shelfSnapshot[i + 1][j] &&
                                shelfSnapshot[i][j] == shelfSnapshot[i + 1][j + 1]) {
                            foundTile = shelfSnapshot[i][j];
                            shelfSnapshot[i][j] = Tile.NOT_VALID;
                            shelfSnapshot[i][j + 1] = Tile.NOT_VALID;
                            shelfSnapshot[i + 1][j] = Tile.NOT_VALID;
                            shelfSnapshot[i + 1][j + 1] = Tile.NOT_VALID;
                        }
                    }
                } else if(foundTile != Tile.NOT_VALID){
                    if (shelfSnapshot[i][j] != Tile.BLANK && shelfSnapshot[i][j] != Tile.NOT_VALID) {
                        if (shelfSnapshot[i][j] == foundTile &&
                                shelfSnapshot[i][j] == shelfSnapshot[i][j + 1] &&
                                shelfSnapshot[i][j] == shelfSnapshot[i + 1][j] &&
                                shelfSnapshot[i][j] == shelfSnapshot[i + 1][j + 1]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
