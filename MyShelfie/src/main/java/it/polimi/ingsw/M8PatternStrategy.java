package it.polimi.ingsw;

/**
 * a class implementing the - 8 cells of the same color - pattern strategy of the corresponding common goal card.
 */

public class M8PatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Color[][] shelfSnapshot) {
        int cont = 0;
        final int maxc = shelfSnapshot.length-1;
        final int maxr = shelfSnapshot[0].length-1;
        for (Color c : Color.values()) {
            for (int i = 0; i < maxr; i++) {
                for (int j = 0; j < maxc; j++) {
                    if (shelfSnapshot[i][j] == c)
                        cont++;
                    if (cont >= 8)
                        return true;
                }
            }
            cont = 0;
        }
        return false;
    }
}