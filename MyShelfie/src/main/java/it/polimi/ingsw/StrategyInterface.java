package it.polimi.ingsw;

/**
 * Interface class implemented by subclasses in order to apply a different method for the search
 * of a pattern in the shelf, according to the rule specified in each common goal card.
 *
 */
public interface StrategyInterface {
    /**
     * the method which encapsulate the strategy adopted by the specific common goal card.
     *
     * @param shelfSnapshot: a snapshot of the current player's shelf
     * @return true: if the shelf contains the pattern,
     *         false: otherwise
     */
    boolean checkPattern(Color[][] shelfSnapshot);

}
