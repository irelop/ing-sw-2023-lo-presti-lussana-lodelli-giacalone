package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.StrategyInterface;
import it.polimi.ingsw.Tile;

import java.util.Arrays;

public class O5DPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int counter = 0;
        int rows = shelfSnapshot.length;

        for (int i = 0; i < rows && counter < 4; i++) {
            if (Arrays.stream(shelfSnapshot[i]).filter(x -> x != Tile.BLANK).count() == 6) {
                if (Arrays.stream(shelfSnapshot[i]).filter(x -> x != Tile.BLANK).distinct().count() <= 3) counter++;
            }
        }

        return (counter == 4);
    }
}
