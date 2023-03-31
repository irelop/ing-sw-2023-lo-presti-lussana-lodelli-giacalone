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
                if(shelfSnapshot[i][j]==shelfSnapshot[i][j+1]) {
                    counter++;
                    shelfSnapshot[i][j] = null;
                    shelfSnapshot[i][j + 1] = null;
                }

                else if(shelfSnapshot[i][j]==shelfSnapshot[i+1][j]) {
                    counter++;
                    shelfSnapshot[i][j] = null;
                    shelfSnapshot[i+1][j] = null;
                }
            }
        }

        return (counter==6);
    }
}
