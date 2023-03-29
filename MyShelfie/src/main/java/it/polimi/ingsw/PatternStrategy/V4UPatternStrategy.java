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
        int[] found = new int[4];

        //set the array with inexistent index of the shelf
        Arrays.fill(found, -4);

        //check if there is a pattern of 4 vertical tile and it hasn't been already counted
        for(int i=0; i<numRow-3; i++)
            for(int j=0; j<numCol; j++){
                if(shelfSnapshot[i][j] == shelfSnapshot[i+1][j] &&
                   shelfSnapshot[i][j] == shelfSnapshot[i+2][j] &&
                   shelfSnapshot[i][j] == shelfSnapshot[i+3][j] &&
                   found[j] + 3 < i){

                    //count and mark on the array
                    contatore++;
                    found[j] = i;
                    if(contatore >= 4) return true;
                }
            }
        return false;
    }
}
