package it.polimi.ingsw.Exceptions;

public class AdjacentException extends Exception {
    public AdjacentException() {
        super();
    }

    @Override
    public String toString() {
        return "You have chosen some cells which have 4 neighbours (cell adjacent to them) remaining";
    }
}
