package it.polimi.ingsw.utils.Exceptions;

public class InvalidReconnectionAnswerException extends Exception{

    public InvalidReconnectionAnswerException() {
        super();
    }

    @Override
    public String toString(){
        return """
                Sorry, the choice inserted is not valid: please select another one.\s
                You have to choose between the continuing a game (C) or creating a new one (N).
                """;
    }
}
