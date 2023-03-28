package it.polimi.ingsw.Exceptions;

public class InvalidDirectionException extends Exception {

    public InvalidDirectionException(){
        super();
    }

    @Override
    public String toString(){
        return "You chose the wrong direction! You can choose between s (south), n (noth), e (east), w (west)";
    }
}
