package it.polimi.ingsw.Server.Model;
/**
 *  Score class: this class wraps the points gained by the player during the game and provide methods to update them.
 */
public class Score {
    private int score;

    public Score() {
        this.score = 0;
    }
    public void addScore(int points) {
        this.score += points;
    }
    public int getScore() {
        return score;
    }
}
