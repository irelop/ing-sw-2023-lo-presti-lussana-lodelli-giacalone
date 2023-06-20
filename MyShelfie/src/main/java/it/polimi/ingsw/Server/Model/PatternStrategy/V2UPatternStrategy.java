package it.polimi.ingsw.Server.Model.PatternStrategy;

import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;


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
        Tile[][] copy = new Tile[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                copy[i][j] = shelfSnapshot[i][j];
            }
        }

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(copy[i][j] != Tile.BLANK && copy[i][j] != Tile.NOT_VALID){
                    if((i+1) < rows && copy[i][j] == copy[i+1][j]){
                        counter++;
                        copy[i][j] = Tile.NOT_VALID;
                        copy[i+1][j] = Tile.NOT_VALID;
                    }
                    else if((j+1) < columns && copy[i][j] == copy[i][j+1]){
                        counter++;
                        copy[i][j] = Tile.NOT_VALID;
                        copy[i][j+1] = Tile.NOT_VALID;
                    }
                }
            }
        }

        return (counter == 6);
    }
}
