package it.polimi.ingsw.Exceptions;

public class InvalidPositionException extends Exception {
    public InvalidPositionException(){
        super();
    }

    @Override
    public String toString(){
        return "The position you chose is not valid! All tiles must have at least one free side!";
    }
}
