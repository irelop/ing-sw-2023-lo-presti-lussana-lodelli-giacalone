package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

import java.util.Arrays;

/**
 * a class implementing the P pattern strategy of the corresponding common goal card.
 */
public class PPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;
        Tile[] firstColumn = new Tile[rows];
        Tile[] decisionColumn = new Tile[rows]; //the second column: it indicates the ascending or descending order

        for (int i = 0; i < rows; i++) {
            firstColumn[i] = shelfSnapshot[i][0];
        }

        for (int i = 0; i < rows; i++) {
            decisionColumn[i] = shelfSnapshot[i][1];
        }
        int numTilesColumn = Math.toIntExact(Arrays.stream(decisionColumn).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count());

        //checking ascending branch
        if (numTilesColumn ==
                Arrays.stream(firstColumn).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count() + 1) {
            for (int j = 2; j < columns; j++) {
                Tile[] tmp = new Tile[rows];
                for (int i = 0; i < rows; i++) {
                    tmp[i] = shelfSnapshot[i][j];
                }
                if ((Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count()) !=
                        numTilesColumn+ 1) {
                    System.out.println(Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count());
                    return false;
                }
                numTilesColumn = Math.toIntExact(Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count());
            }
            return true;
        }


        //checking descending branch
        else if (numTilesColumn ==
                Arrays.stream(firstColumn).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count() - 1) {
            for (int j = 2; j < columns; j++) {
                Tile[] tmp = new Tile[rows];
                for (int i = 0; i < rows; i++) {
                    tmp[i] = shelfSnapshot[i][j];
                }
                if ((Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count()) !=
                        numTilesColumn - 1){
                    return false;
                }
                numTilesColumn = Math.toIntExact(Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count());
            }
            return true;
        }

        else return false;
    }
}
    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /*ATTENTION: NOT WORKING - DOESN'T CHECK THE FALSE CASE
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
        return false;*/
