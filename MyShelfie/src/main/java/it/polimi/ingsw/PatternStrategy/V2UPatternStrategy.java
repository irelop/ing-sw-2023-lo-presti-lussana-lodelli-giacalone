package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

public class V2UPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int counter = 0;

        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;

        for(int i = 0; i<rows-1; i++){
            for(int j = 0; j< columns -1; j++){

                //checking the adjacent cell on the right
                if(shelfSnapshot[i][j] != Tile.BLANK && shelfSnapshot[i][j] != Tile.NOT_VALID) {
                    if (shelfSnapshot[i][j] == shelfSnapshot[i][j + 1]) {
                        counter++;
                        shelfSnapshot[i][j] = Tile.NOT_VALID;
                        shelfSnapshot[i][j + 1] = Tile.NOT_VALID;
                    } else if (shelfSnapshot[i][j] == shelfSnapshot[i + 1][j]) {
                        counter++;
                        shelfSnapshot[i][j] = Tile.NOT_VALID;
                        shelfSnapshot[i + 1][j] = Tile.NOT_VALID;
                    }
                }
            }
        }

        return (counter==6);
    }
}
