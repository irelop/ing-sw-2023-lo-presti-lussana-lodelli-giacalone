package it.polimi.ingsw.Server.Model.Exceptions;

/**
 * Exception thrown when the player chooses an invalid column index while
 * inserting tiles in his shelf
 *
 * @author Riccardo Lodelli
 */

public class InvalidShelfColumnException extends Exception {

    public InvalidShelfColumnException(){
        super();
    }

    @Override
    public String toString(){
        return "You chose a column which doesn't exist!";
    }
}