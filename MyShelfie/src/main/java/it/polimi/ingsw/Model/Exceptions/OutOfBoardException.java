package it.polimi.ingsw.Model.Exceptions;

/**
 * Exception for the choice of the tiles from the board. It is thrown if the insert index is not between 1 and 9
 *
 * @author Irene Lo Presti
 */

public class OutOfBoardException extends Exception {

    public OutOfBoardException(){
        super();
    }

    @Override
    public String toString(){
        return "Chosen cells cannot be outside of the board. Remember that the index are " +
                "between 1 and 9!";
    }
}
