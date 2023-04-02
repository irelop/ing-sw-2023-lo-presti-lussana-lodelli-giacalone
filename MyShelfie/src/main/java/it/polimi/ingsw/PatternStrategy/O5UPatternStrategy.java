package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

import java.util.Arrays;

public class O5UPatternStrategy implements StrategyInterface {

    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int counter = 0;
        int rows = shelfSnapshot.length;

        for(int i = 0; i< rows && counter < 2;i++){
            if(Arrays.stream(shelfSnapshot[i]).filter(x->x!=Tile.BLANK && x!= Tile.NOT_VALID).distinct().count()>=5) counter++;
        }

        return (counter ==2);
    }
}
