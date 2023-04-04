package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

import java.util.Arrays;

/**
 * a class implementing the four vertical tile with same color pattern strategy of the corresponding common goal card.
 */
public class V4UPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int numRow = shelfSnapshot.length;
        int numCol = shelfSnapshot[0].length;
        int contatore = 0;
        int[] foundVertical = new int[numCol];
        int[] foundHorizontal = new int[numRow];

        //set the array with inexistent index of the shelf
        Arrays.fill(foundVertical, -4);

        //check if there is a pattern of 4 vertical tile and it hasn't been already counted
        for(int i=0; i<numRow-3; i++)
            for(int j=0; j<numCol; j++){
                if(shelfSnapshot[i][j] != Tile.BLANK &&
                        shelfSnapshot[i][j] != Tile.NOT_VALID &&
                        shelfSnapshot[i][j] == shelfSnapshot[i+1][j] &&
                        shelfSnapshot[i][j] == shelfSnapshot[i+2][j] &&
                        shelfSnapshot[i][j] == shelfSnapshot[i+3][j] &&
                        foundVertical[j] + 3 < i)
                {

                    //count and mark on the array
                    contatore++;
                    foundVertical[j] = i;
                    if(contatore == 4) return true;
                }
            }
        //check if there is a pattern of 4 horizontal tile and it hasn't been already counted
        Arrays.fill(foundHorizontal, -4);
        for(int i=0; i<numRow; i++)
            for(int j=0; j<numCol-3; j++){
                if(shelfSnapshot[i][j] != Tile.BLANK &&
                        shelfSnapshot[i][j] != Tile.NOT_VALID &&
                        shelfSnapshot[i][j] == shelfSnapshot[i][j+1] &&
                        shelfSnapshot[i][j] == shelfSnapshot[i][j+2] &&
                        shelfSnapshot[i][j] == shelfSnapshot[i][j+3] &&
                        foundHorizontal[i] +3 < j )
                {

                    //count and mark on the array
                    contatore++;
                    foundHorizontal[i] = j;
                    if(contatore == 4) return true;
                }
            }
        return false;
    }
}
