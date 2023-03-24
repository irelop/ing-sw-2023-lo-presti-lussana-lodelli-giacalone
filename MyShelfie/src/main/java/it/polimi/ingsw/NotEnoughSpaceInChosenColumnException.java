package it.polimi.ingsw;

public class NotEnoughSpaceInChosenColumnException extends Exception {
    public NotEnoughSpaceInChosenColumnException(){
        super();
    }
    @Override
    public String toString(){
        return "There is not enough space in this column, you should choose another one!";
    }
}
