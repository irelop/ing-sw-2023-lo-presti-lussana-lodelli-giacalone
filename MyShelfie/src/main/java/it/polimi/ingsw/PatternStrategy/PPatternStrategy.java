package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

/**
 * a class implementing the P pattern strategy of the corresponding common goal card.
 */
public class PPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int numCol = shelfSnapshot[0].length;

        //check from the west
        if(shelfSnapshot[1][0]!=Tile.BLANK && shelfSnapshot[1][0]!=Tile.NOT_VALID){
            for(int i = 1;i<numCol;i++){
                if(shelfSnapshot[1+i][i]==Tile.BLANK){
                    return false;
                }
            }
            return true;
        }

        //check from the east
        else if(shelfSnapshot[1][4]!=Tile.BLANK && shelfSnapshot[1][4]!=Tile.NOT_VALID){
            for(int i=1;i<numCol;i++){
                if(shelfSnapshot[i+1][numCol-1-i]==Tile.BLANK)
                    return false;
            }
            return true;
        }
        return false;
    }
}
