package it.polimi.ingsw;

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
