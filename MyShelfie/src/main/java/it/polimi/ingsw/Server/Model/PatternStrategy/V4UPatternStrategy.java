package it.polimi.ingsw.Server.Model.PatternStrategy;

import it.polimi.ingsw.Server.Model.StrategyInterface;
import it.polimi.ingsw.Server.Model.Tile;

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
        Tile[][] copy = new Tile[numRow][numCol];
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numCol; j++) {
                copy[i][j] = shelfSnapshot[i][j];
            }
        }
        int contatore = 0;


        //check if there is a pattern of 4 vertical tile, and it hasn't been already counted
        for(int i=0; i<numRow-3; i++)
            for(int j=0; j<numCol; j++){
                if(copy[i][j] != Tile.BLANK &&
                        copy[i][j] != Tile.NOT_VALID &&
                        copy[i][j] == copy[i+1][j] &&
                        copy[i][j] == copy[i+2][j] &&
                        copy[i][j] == copy[i+3][j])
                {

                    //count and mark on the cells
                    copy[i][j] = Tile.NOT_VALID;
                    copy[i+1][j] = Tile.NOT_VALID;
                    copy[i+2][j] = Tile.NOT_VALID;
                    copy[i+3][j] = Tile.NOT_VALID;
                    contatore++;
                    if(contatore == 4) return true;
                }
            }
        for(int i=0; i<numRow; i++)
            for(int j=0; j<numCol-3; j++){
                if(copy[i][j] != Tile.BLANK &&
                        copy[i][j] != Tile.NOT_VALID &&
                        copy[i][j] == copy[i][j+1] &&
                        copy[i][j] == copy[i][j+2] &&
                        copy[i][j] == copy[i][j+3])
                {

                    //count and mark on the cells
                    copy[i][j] = Tile.NOT_VALID;
                    copy[i][j+1] = Tile.NOT_VALID;
                    copy[i][j+2] = Tile.NOT_VALID;
                    copy[i][j+3] = Tile.NOT_VALID;
                    contatore++;
                    if(contatore == 4) return true;
                }
            }
        return false;
    }
}
