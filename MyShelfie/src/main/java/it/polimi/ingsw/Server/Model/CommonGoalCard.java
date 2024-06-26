package it.polimi.ingsw.Server.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * CommonGoalCard class: this class represents a single common goal card wrapping its specific strategy.
 *
 * @author Andrea Giacalone
 */
public class CommonGoalCard implements Serializable {
    private final StrategyInterface commonStrategy;
    private ArrayList<Integer> availableScore;
    private String patternName;
    private CommonCardInfo cardInfo;

    public CommonGoalCard(StrategyInterface commonStrategy) {
        this.commonStrategy = commonStrategy;
        this.patternName = this.getStrategyName();
        this.availableScore = new ArrayList<>(Arrays.asList(8, 6, 4, 2));
        this.cardInfo = new CommonCardInfo(patternName);
    }


    /**
     * Constructor for test files that set manually all the attributes
     * @param commonStrategy: commonGoalCard strategyPattern
     * @param name: commonGoalCard name
     * @param schema: commonGoalCard grid schema
     * @param times: how many times schema must appear in the shelf
     * @param description: short commonGoalCard description
     */
    public CommonGoalCard(StrategyInterface commonStrategy,String name,Tile[][] schema,int times, String description){
        this.commonStrategy = commonStrategy;
        this.patternName = this.getStrategyName();
        this.availableScore = new ArrayList<>(Arrays.asList(8,6,4,2));
        this.cardInfo = new CommonCardInfo(name,schema,times,description);
    }

    public CommonCardInfo getCardInfo(){
        return this.cardInfo;
    }

    /**
     * OVERVIEW: this method returns the score, which depends on from the timing of players' goal achievement.
     * @return score: the available score for the completed goal.
     */
    public int getScore(){
        return availableScore.remove(0);
    }

    public StrategyInterface getCommonStrategy() {
        return commonStrategy;
    }

    /**
     * OVERVIEW: this method allows to check if, given a snapshot of the player's shelf, the strategy pattern of tiles
     * associated to the card matches or not with the position of tiles in the shelf.
     * @param shelfSnapshot: a snapshot of the player's shelf.
     * @return true if it maches with the pattern, false otherwise.
     */
    public boolean checkPattern(Tile[][] shelfSnapshot){
        return (this.commonStrategy.checkPattern(shelfSnapshot));
    }

    public String getStrategyName(){
        return this.commonStrategy.getClass().getName();
    }
    public ArrayList<Integer> returnScore(){
        return availableScore;
    }

    public String printAvailableScore(){
        if(this.availableScore.isEmpty()) return "0";
        return Integer.toString( this.availableScore.get(0));
    }

}
