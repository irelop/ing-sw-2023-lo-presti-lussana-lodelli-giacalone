package it.polimi.ingsw.utils.Exceptions;

public class InvalidRuleAnswerException extends Exception{
    public InvalidRuleAnswerException(){
        super();
    }

    @Override
    public String toString(){
        return """
                Sorry, the choice inserted is not valid: please select another one.\s
                You have to choose between yes (Y) and no (N).
                """;
    }
}
