package it.polimi.ingsw.utils.Exceptions;

public class InvalidReconnectionAnswerException extends Exception{

    public InvalidReconnectionAnswerException() {
        super();
    }

    @Override
    public String toString(){
        return "Sorry, the choice inserted is not valid: please select another one. \n" +
                "You have to choose between the lobby (L) and the game (G).\n";
    }
}
