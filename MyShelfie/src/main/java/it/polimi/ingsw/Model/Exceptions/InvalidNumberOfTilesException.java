package it.polimi.ingsw.Model.Exceptions;

/**
 * Exception for the choice of the tiles from the board. It is thrown if the insert number of tiles is not
 * between 1 and the maximum number of tiles that the player can choose.
 *
 * @author Irene Lo Presti
 */

public class InvalidNumberOfTilesException extends Exception {

    private int maxTilesPickable;
    public InvalidNumberOfTilesException(int maxTilesPickable){
        super();
        this.maxTilesPickable = maxTilesPickable;
    }

    @Override
    public String toString(){
        return "This number is not valid. You must choose a number between 0 and "+(maxTilesPickable-1);
    }
}
