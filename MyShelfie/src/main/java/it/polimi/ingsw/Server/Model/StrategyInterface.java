package it.polimi.ingsw.Server.Model;

import java.io.Serializable;

/**
 * Interface class implemented by subclasses in order to apply a different method for the search
 * of a pattern in the shelf, according to the rule specified in each common goal card.
 *
 */
public interface StrategyInterface extends Serializable {
    /**
     * the method which encapsulate the strategy adopted by the specific common goal card.
     *
     * @param shelfSnapshot: a snapshot of the current player's shelf
     * @return true: if the shelf contains the pattern,
     *         false: otherwise
     */
    boolean checkPattern(Tile[][] shelfSnapshot);

}
