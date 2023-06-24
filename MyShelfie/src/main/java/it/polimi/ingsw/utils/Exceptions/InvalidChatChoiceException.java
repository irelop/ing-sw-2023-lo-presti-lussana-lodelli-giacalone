package it.polimi.ingsw.utils.Exceptions;

public class InvalidChatChoiceException extends Exception{
    @Override
    public String toString() {
        return """
                Sorry, the choice inserted is not valid: please select another one.\s
                You have to choose if you want to chat (Y) or not (N).
                """;
    }
}
