package it.polimi.ingsw;

/**
 * a class implementing the X pattern strategy of the corresponding common goal card.
 */
public class XPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Color[][] shelfSnapshot) {
        int numRow = shelfSnapshot.length;
        int numCol = shelfSnapshot[0].length;

        for(int i=0; i<numRow; i++){
            for(int j=0; j<numCol;j++){
               if(shelfSnapshot[i][j]==shelfSnapshot[i][j+2] && shelfSnapshot[i][j]==shelfSnapshot[i+1][j+1] && shelfSnapshot[i][j]==shelfSnapshot[i+2][j] && shelfSnapshot[i][j]==shelfSnapshot[i+2][j+2])
                   return true;
            }
        }
        return false;
    }
}
