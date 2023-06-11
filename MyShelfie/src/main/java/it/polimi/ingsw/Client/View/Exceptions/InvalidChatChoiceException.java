package it.polimi.ingsw.Client.View.Exceptions;

public class InvalidChatChoiceException extends Exception{
    @Override
    public String toString() {
        return "Sorry, the choice inserted is not valid: please select another one. \n" +
                "You have to choose if you want to chat (Y) or not (N).\n";
    }
}
