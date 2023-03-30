package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

import java.util.Arrays;

public class Q2UPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int counter = 0;
        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;

        for(int i=0;i<rows-1 && counter <2;i++){
            for(int j=0;j<columns-1;j++){
                if(shelfSnapshot[i][j] != Tile.BLANK && shelfSnapshot[i][j]==shelfSnapshot[i][j+1] && shelfSnapshot[i][j]==shelfSnapshot[i+1][j] && shelfSnapshot[i][j]==shelfSnapshot[i+1][j+1]){
                    counter++;
                    shelfSnapshot[i][j]=null;
                    shelfSnapshot[i][j+1]=null;
                    shelfSnapshot[i+1][j]=null;
                    shelfSnapshot[i+1][j+1]=null;
                }
            }
        }

        if(counter==2) return true;

        return false;
    }
}
