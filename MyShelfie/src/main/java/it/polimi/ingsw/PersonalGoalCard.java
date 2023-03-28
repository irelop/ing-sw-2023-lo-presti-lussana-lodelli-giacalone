package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Arrays;

public class PersonalGoalCard {

    private final static int maxr = 5;
    private final static int maxc = 6;
    private Tile[][] pattern;
    private ArrayList<Integer> availableScore;

    public PersonalGoalCard() {
        // availableScore contains the amount of points gained when the player puts
        // the correct color in the correct position, given by the personal goal card
        availableScore = new ArrayList<>(Arrays.asList(1,1,2,2,3,3));
        pattern = new Tile[maxr][maxc];
        for (int i = 0; i < maxr; i++) {
            for (int j = 0; j < maxc; j++) {
                pattern[i][j] = Tile.BLANK;
            }
        }
    }

    public int getScore() {
        return availableScore.remove(0);
    }

    private int getPersonalGoalScore(Tile[][] shelfSnapshot) {

        for (int i = 0; i < maxr; i++) {
            for (int j = 0; j < maxc; j++) {
                if (shelfSnapshot[i][j] == pattern[i][j]) {
                    pattern[i][j] = Tile.BLANK;
                    return getScore();
                }
            }
        }
        return 0;
    }
}
