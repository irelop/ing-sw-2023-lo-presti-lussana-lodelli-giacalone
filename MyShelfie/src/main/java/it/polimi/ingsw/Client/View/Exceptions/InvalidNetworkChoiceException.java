package it.polimi.ingsw.Client.View.Exceptions;

public class InvalidNetworkChoiceException extends Exception{
    public InvalidNetworkChoiceException(){
        super();
    }

    @Override
    public String toString(){
        return "Sorry, the choice inserted is not valid: please select another one. \n" +
                "You have to choose between RMI connection (R) and Socket connection (S).\n";
    }
}
