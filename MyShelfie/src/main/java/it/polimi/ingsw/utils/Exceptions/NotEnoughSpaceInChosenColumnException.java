package it.polimi.ingsw.utils.Exceptions;

/**
 * Exception for the choice of the column of the player's shelf. It is thrown if the player chooses a column
 * which does not have enough space for the tiles that he/she chose from the board.
 *
 * @author Matteo Lussana, Irene Lo Presti
 */

public class NotEnoughSpaceInChosenColumnException extends Exception {
    public NotEnoughSpaceInChosenColumnException(){
        super();
    }
    @Override
    public String toString(){
        return "There is not enough space in this column, you should choose another one!";
    }
}
