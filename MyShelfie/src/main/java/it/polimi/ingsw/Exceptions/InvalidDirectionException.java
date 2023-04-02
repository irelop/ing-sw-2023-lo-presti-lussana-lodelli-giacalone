package it.polimi.ingsw.Exceptions;

/**
 * Exception for the choice of the tiles from the board. It is thrown if the insert direction is not a letter
 * between n,s,w,e.
 *
 * @author Irene Lo Presti
 */
public class InvalidDirectionException extends Exception {

    public InvalidDirectionException(){
        super();
    }

    @Override
    public String toString(){
        return "You chose the wrong direction! " +
                "Remember: you can choose between s (south), n (north), e (east), w (west) and you cannot " +
                "go outside the edges of the board.";
    }
}
