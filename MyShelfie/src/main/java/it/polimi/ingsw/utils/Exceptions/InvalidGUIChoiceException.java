package it.polimi.ingsw.utils.Exceptions;

public class InvalidGUIChoiceException  extends Exception {
    @Override
    public String toString() {
        return """
                Sorry, the choice inserted is not valid: please select another one.\s
                You have to choose if you want the Graphic User Interface (Y) or not (N).
                """;
    }
}

