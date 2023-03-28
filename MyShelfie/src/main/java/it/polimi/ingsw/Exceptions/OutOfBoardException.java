package it.polimi.ingsw.Exceptions;

public class OutOfBoardException extends Exception {

    public OutOfBoardException(){
        super();
    }

    @Override
    public String toString(){
        return "You chose a position outside the board, the index must be between 1 and 9";
    }
}
