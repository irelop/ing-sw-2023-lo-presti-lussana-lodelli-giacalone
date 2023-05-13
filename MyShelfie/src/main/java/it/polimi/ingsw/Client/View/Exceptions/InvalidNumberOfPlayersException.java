package it.polimi.ingsw.Client.View.Exceptions;


public class InvalidNumberOfPlayersException extends Exception {

    public InvalidNumberOfPlayersException(){
        super();
    }

    @Override
    public String toString(){
        return "Sorry, the number inserted is not valid: please select another one. \n" +
                "You have to choose between 2 and 4.";
    }
}
