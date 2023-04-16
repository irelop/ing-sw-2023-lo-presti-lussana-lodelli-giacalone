package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;


/**
 * V2UPatternStrategy class: this class represents the strategy of the common goal card which checks if there are 6
 *                           separated groups, each one formed by 2 adjacent tiles of the same type.
 *                           Different groups can be filled with different type of tiles.
 *
 * @author Andrea Giacalone
 */
public class V2UPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int counter = 0;
        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(shelfSnapshot[i][j] != Tile.BLANK && shelfSnapshot[i][j] != Tile.NOT_VALID){
                    if((i+1) < rows && shelfSnapshot[i][j] == shelfSnapshot[i+1][j]){
                        counter++;
                        shelfSnapshot[i][j] = Tile.NOT_VALID;
                        shelfSnapshot[i+1][j] = Tile.NOT_VALID;
                    }
                    else if((j+1) < columns && shelfSnapshot[i][j] == shelfSnapshot[i][j+1]){
                        counter++;
                        shelfSnapshot[i][j] = Tile.NOT_VALID;
                        shelfSnapshot[i][j+1] = Tile.NOT_VALID;
                    }
                }
            }
        }

        return (counter == 6);
    }
}
