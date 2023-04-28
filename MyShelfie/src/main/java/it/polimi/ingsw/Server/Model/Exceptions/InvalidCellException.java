package it.polimi.ingsw.Server.Model.Exceptions;

/**
 * Exception for the choice of the tiles from the board. It is thrown if one of the chosen cell is not valid.
 *
 * @author Irene Lo Presti
 */


public class InvalidCellException extends Exception {
    public InvalidCellException(){
        super();
    }

    @Override
    public String toString(){
        return "This cell is not in the board! Choose another one.";
    }
}
