package it.polimi.ingsw.Server.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents a single personal goal card with its specific color pattern.
 *
 * @author Riccardo Lodelli
 */
public class PersonalGoalCard implements Serializable {

    String id;
    private final static int maxr = 6;
    private final static int maxc = 5;
    private Tile[][] pattern;
    private ArrayList<Integer> availableScore;

    public PersonalGoalCard(Tile[][] givenPattern) {
        availableScore = new ArrayList<>(Arrays.asList(1,1,2,2,3,3));
        this.pattern = givenPattern;
    }

    public Tile[][] getPattern() {
        return pattern;
    }

    public String getId() {
        return id;
    }

    public PersonalGoalCard() {
        // availableScore contains the amount of points gained when the player puts
        // the correct color in the correct position, given by the personal goal card
        availableScore = new ArrayList<>(Arrays.asList(1,1,2,2,3,3));
        pattern = new Tile[maxr][maxc];

        id = ReadFileByLines.getLine();

        for (int i = 0; i < maxr; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < maxc; j++)
                pattern[i][j] = Tile.valueOf(values[j]);
        }

    }

    /**
     *  This method removes the head of availableScore in order to give points to the player who put a tile
     *  in the place indicated on the personal goal card
     */
    private int getScore() {
        return availableScore.remove(0);
    }

    /**
     * Check if the current player puts a tile in the right place and returns the amount of points gained
     * from this move
     * @param shelfSnapshot: a snapshot of the current player's shelf
     * @return an int given by the head of availableScore
     */
    public int getPersonalGoalScore(Tile[][] shelfSnapshot) {

        for (int i = 0; i < maxr; i++) {
            for (int j = 0; j < maxc; j++) {
                if (shelfSnapshot[i][j] == pattern[i][j] && pattern[i][j] != Tile.BLANK) {
                    pattern[i][j] = Tile.BLANK;
                    return getScore();
                }
            }
        }
        return 0;
    }
}
