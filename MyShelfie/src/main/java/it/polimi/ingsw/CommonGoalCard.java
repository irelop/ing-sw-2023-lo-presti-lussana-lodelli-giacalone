package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents a single common goal card wrapping its specific strategy.
 */
public class CommonGoalCard {
    private final StrategyInterface commonStrategy;
    private ArrayList<Integer> availableScore;

    public CommonGoalCard(StrategyInterface commonStrategy){
        this.commonStrategy = commonStrategy;
        this.availableScore = new ArrayList<>(Arrays.asList(8,6,4,2));
    }

    /**
     * A method that returns the score which depends from the timing of players' gaol achievement.
     * @return score: the available score for the completed goal.
     */
    public int getScore(){
        return availableScore.remove(0);
    }

    /* -o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-

        ALTERNATIVA PROPOSTA DA RICCARDO CON CODA O PILA

    public class CommonGoalCard {
        private StrategyInterface commonStrategy;
        private Stack<Integer> availableScore;

        public CommonGoalCard(StrategyInterface commonStrategy){
            this.commonStrategy = commonStrategy;
            for(int i=0; i<4; i++){
                availableScore.push(8-2*i);
            }
        }

        public int getScore(){
            return (availableScore.pop());
        }
    }
    */
}
