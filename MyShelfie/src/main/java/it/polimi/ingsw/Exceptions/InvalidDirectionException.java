package it.polimi.ingsw.Exceptions;

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
