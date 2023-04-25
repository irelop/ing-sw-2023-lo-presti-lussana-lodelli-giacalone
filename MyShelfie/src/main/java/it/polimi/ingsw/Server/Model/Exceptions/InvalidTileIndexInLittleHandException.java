package it.polimi.ingsw.Server.Model.Exceptions;

/**
 * Exception for the choice of the order of the tiles chosen from the board.
 * It is thrown if the player insert the same number or a number that is not between 1 and the number of tile
 * that he/she chose.
 *
 * @author Matteo Lussana, Irene Lo Presti
 */

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
