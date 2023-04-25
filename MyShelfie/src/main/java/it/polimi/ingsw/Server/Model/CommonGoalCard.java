package it.polimi.ingsw.Server.Model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * CommonGoalCard class: this class represents a single common goal card wrapping its specific strategy.
 *
 * @author Andrea Giacalone
 */
public class CommonGoalCard {
    private final StrategyInterface commonStrategy;
    private ArrayList<Integer> availableScore;

    public CommonGoalCard(StrategyInterface commonStrategy){
        this.commonStrategy = commonStrategy;
        this.availableScore = new ArrayList<>(Arrays.asList(8,6,4,2));
    }

    /**
     * OVERVIEW: this method returns the score, which depends from the timing of players' gaol achievement.
     * @return score: the available score for the completed goal.
     */
    public int getScore(){
        return availableScore.remove(0);
    }

    public StrategyInterface getCommonStrategy() {
        return commonStrategy;
    }

    public boolean checkPattern(Tile[][] shelfSnapshot){
        return (this.commonStrategy.checkPattern(shelfSnapshot));
    }

    public String getStrategyName(){
        return this.commonStrategy.getClass().getName();
    }
}
