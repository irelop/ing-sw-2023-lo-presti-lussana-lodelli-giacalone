package it.polimi.ingsw.Server.Model.Exceptions;

/**
 * Exception for the choice of the tiles from the board. It is thrown if one of the chosen cell is empty.
 *
 * @author Irene Lo Presti
 */

public class EmptyCellException extends Exception {
    public EmptyCellException(){
        super();
    }

    @Override
    public String toString(){
        return "This cell is empty! Choose another one.";
    }
}
