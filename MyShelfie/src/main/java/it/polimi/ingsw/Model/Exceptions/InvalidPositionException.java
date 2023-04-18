package it.polimi.ingsw.Model.Exceptions;

/**
 * Exception for the choice of the tiles from the board. It is thrown if one of the chosen tile has not a
 * free side.
 *
 * @author Irene Lo Presti
 */

public class InvalidPositionException extends Exception {
    public InvalidPositionException(){
        super();
    }

    @Override
    public String toString(){
        return "The position you chose is not valid! All tiles must have at least one free side!";
    }
}
