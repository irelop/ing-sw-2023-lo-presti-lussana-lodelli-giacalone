package it.polimi.ingsw;

/**
 * a class implementing the Angular pattern strategy of the corresponding common goal card.
 */

public class APatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Color[][] shelfSnapshot) {
        final int max = shelfSnapshot.length-1;
        return shelfSnapshot[0][0] == shelfSnapshot[0][max] && shelfSnapshot[0][0] == shelfSnapshot[max][0] && shelfSnapshot[0][0] == shelfSnapshot[max][max];
    }
}
