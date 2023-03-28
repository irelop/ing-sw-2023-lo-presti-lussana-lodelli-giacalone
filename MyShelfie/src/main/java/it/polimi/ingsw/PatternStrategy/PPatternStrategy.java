package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

import java.util.ArrayList;

public class PPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        ArrayList<Integer> counters;
        int numRow = shelfSnapshot.length;
        int numCol = shelfSnapshot[0].length;
        /*entra in una colonna
        appena trova che la prima coppia di tile di colore uguale inizia a sommare
        alla prima che trova diversa smette di sommare
         */
        if(shelfSnapshot[1][0]!=Tile.BLANK){
            for(int i = 1;i<5;i++){
                if(shelfSnapshot[1+i][0+i]==Tile.BLANK){
                    return false;
                }
            }
            return true;
        }
        else if(shelfSnapshot[1][4]!=Tile.BLANK){
            for(int i=1;i<5;i++){
                if(shelfSnapshot[1+1][4-i]==Tile.BLANK)
                    return false;
            }
            return true;
        }
        return false;
    }
}
