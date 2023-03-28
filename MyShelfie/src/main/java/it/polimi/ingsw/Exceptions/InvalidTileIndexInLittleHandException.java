package it.polimi.ingsw.Exceptions;

public class InvalidTileIndexInLittleHandException extends Exception {

    int tilesNumber;

    public InvalidTileIndexInLittleHandException(int tilesNumber){
        super();
        this.tilesNumber = tilesNumber;
    }

    @Override
    public String toString(){
        return "The choice is not valid, choose a number, without repetitions and between 1 and "+tilesNumber;
    }
}
