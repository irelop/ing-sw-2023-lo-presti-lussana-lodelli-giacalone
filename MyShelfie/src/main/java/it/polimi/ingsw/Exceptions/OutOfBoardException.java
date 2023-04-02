package it.polimi.ingsw.Exceptions;

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
        return "You chose a position outside the board, the index must be between 1 and 9";
    }
}
