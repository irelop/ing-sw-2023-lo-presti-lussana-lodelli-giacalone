package it.polimi.ingsw.Exceptions;

public class InvalidChoiceException extends Exception {
    public InvalidChoiceException(boolean exCase) {
        if (exCase) {
            System.out.println("You have chosen an invalid cell");
        } else {
            System.out.println("You can't pick because there are still neighbours");
        }
    }
}
