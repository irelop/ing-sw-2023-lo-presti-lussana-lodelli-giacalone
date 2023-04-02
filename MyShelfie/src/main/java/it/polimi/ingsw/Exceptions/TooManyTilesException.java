package it.polimi.ingsw.Exceptions;

public class TooManyTilesException extends Exception {
    public TooManyTilesException(){super();}

    @Override
    public String toString(){
        return "You have chosen too many tiles! You don't have enough space in your shelf";
    }
}
