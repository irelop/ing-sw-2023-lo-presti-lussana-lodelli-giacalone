package it.polimi.ingsw;

/**
 * a class implementing the Angular pattern strategy of the corresponding common goal card.
 */

public class APatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Color[][] shelfSnapshot) {
        final int maxc = shelfSnapshot.length-1;
        final int maxr = shelfSnapshot[0].length-1;
        return shelfSnapshot[0][0] == shelfSnapshot[0][maxc] && shelfSnapshot[0][0] == shelfSnapshot[maxr][0] && shelfSnapshot[0][0] == shelfSnapshot[maxr][maxc];
    }
}
