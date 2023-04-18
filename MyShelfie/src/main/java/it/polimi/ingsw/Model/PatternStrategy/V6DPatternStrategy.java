package it.polimi.ingsw.Model.PatternStrategy;

import it.polimi.ingsw.Model.StrategyInterface;
import it.polimi.ingsw.Model.Tile;

import java.util.Arrays;

/**
 * V6DPatternStrategy class: this class represents the strategy of the common goal card which checks if there are 3
 *                           columns, each one filled by 6 tiles of max 3 different types.
 *                           Each column can have a random combination of different types of tiles.
 *
 * @author Andrea Giacalone
 */
public class V6DPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int counter = 0;
        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;
        Tile[] tmp = new Tile[rows];

        /* I need to use a temporary array in order to save the chosen column, then I'll proceed to count the
           distinct elements
         */

        for(int j=0;j<columns && counter < 3;j++){
            for(int i=0;i<rows;i++){
                tmp[i] = shelfSnapshot[i][j];
            }
            //first I need to check if there are 6 tiles
            if(Arrays.stream(tmp).filter(x->x!=Tile.BLANK).count()==6) {
                //then I'll check the types
                if (Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x !=Tile.NOT_VALID).distinct().count() <= 3) counter++;
            }
        }

        return (counter==3);
    }
}
