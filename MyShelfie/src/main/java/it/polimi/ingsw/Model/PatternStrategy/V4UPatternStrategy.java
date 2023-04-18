package it.polimi.ingsw.Model.PatternStrategy;

import it.polimi.ingsw.Model.StrategyInterface;
import it.polimi.ingsw.Model.Tile;

/**
 * V2UPatternStrategy class: this class represents the strategy of the common goal card which checks if there are 4
 *                           separated groups, each one formed by 4 adjacent tiles of the same type.
 *                           Different groups can be filled with different type of tiles.
 *
 * @author Matteo Lussana
 */
public class V4UPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int numRow = shelfSnapshot.length;
        int numCol = shelfSnapshot[0].length;
        int contatore = 0;


        //check if there is a pattern of 4 vertical tile and it hasn't been already counted
        for(int i=0; i<numRow-3; i++)
            for(int j=0; j<numCol; j++){
                if(shelfSnapshot[i][j] != Tile.BLANK &&
                        shelfSnapshot[i][j] != Tile.NOT_VALID &&
                        shelfSnapshot[i][j] == shelfSnapshot[i+1][j] &&
                        shelfSnapshot[i][j] == shelfSnapshot[i+2][j] &&
                        shelfSnapshot[i][j] == shelfSnapshot[i+3][j])
                {

                    //count and mark on the cells
                    shelfSnapshot[i][j] = Tile.NOT_VALID;
                    shelfSnapshot[i+1][j] = Tile.NOT_VALID;
                    shelfSnapshot[i+2][j] = Tile.NOT_VALID;
                    shelfSnapshot[i+3][j] = Tile.NOT_VALID;
                    contatore++;
                    if(contatore == 4) return true;
                }
            }

        for(int i=0; i<numRow; i++)
            for(int j=0; j<numCol-3; j++){
                if(shelfSnapshot[i][j] != Tile.BLANK &&
                        shelfSnapshot[i][j] != Tile.NOT_VALID &&
                        shelfSnapshot[i][j] == shelfSnapshot[i][j+1] &&
                        shelfSnapshot[i][j] == shelfSnapshot[i][j+2] &&
                        shelfSnapshot[i][j] == shelfSnapshot[i][j+3])
                {

                    //count and mark on the cells
                    shelfSnapshot[i][j] = Tile.NOT_VALID;
                    shelfSnapshot[i][j+1] = Tile.NOT_VALID;
                    shelfSnapshot[i][j+2] = Tile.NOT_VALID;
                    shelfSnapshot[i][j+3] = Tile.NOT_VALID;
                    contatore++;
                    if(contatore == 4) return true;
                }
            }
        return false;
    }
}
