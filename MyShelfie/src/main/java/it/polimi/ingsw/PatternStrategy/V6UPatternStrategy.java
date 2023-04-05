package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

import java.util.Arrays;

public class V6UPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;
        Tile[] tmp = new Tile[rows];

        /* I need to use a temporary array in order to save the chosen column, then I'll proceed to count the
           distinct elements
         */
        int counter =0;
        for(int j=0;j<columns;j++){
            for(int i=0;i<rows;i++){
                tmp[i] = shelfSnapshot[i][j];
            }

            if(Arrays.stream(tmp).filter(x->x!=Tile.BLANK && x!= Tile.NOT_VALID).distinct().count()==6) {
                counter++;
            }
        }

        return counter==2;

    }
}
