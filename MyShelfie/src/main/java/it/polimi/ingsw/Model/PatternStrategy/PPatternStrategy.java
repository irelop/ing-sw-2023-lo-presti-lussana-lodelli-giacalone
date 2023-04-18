package it.polimi.ingsw.Model.PatternStrategy;

import it.polimi.ingsw.Model.StrategyInterface;
import it.polimi.ingsw.Model.Tile;

import java.util.Arrays;

/**
 * PPatternStrategy class: this class represents the strategy of the common goal card which checks if there are columns
 *                         whose heights follow an ascendent or descendent order, without any restriction on the type
 *                         of tiles filling the column: the key point is that, chosen the order, each column must have
 *                         one ano one only tile more than the precedent one.
 *
 * @author Andrea Giacalone, Riccardo Lodelli
 */
public class PPatternStrategy implements StrategyInterface {
    @Override
    public boolean checkPattern(Tile[][] shelfSnapshot) {
        int rows = shelfSnapshot.length;
        int columns = shelfSnapshot[0].length;
        Tile[] firstColumn = new Tile[rows];
        Tile[] decisionColumn = new Tile[rows]; //the second column: it indicates the ascending or descending order

        for (int i = 0; i < rows; i++) {
            firstColumn[i] = shelfSnapshot[i][0];
        }

        for (int i = 0; i < rows; i++) {
            decisionColumn[i] = shelfSnapshot[i][1];
        }
        int numTilesColumn = Math.toIntExact(Arrays.stream(decisionColumn).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count());

        //checking ascending branch
        if (numTilesColumn ==
                Arrays.stream(firstColumn).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count() + 1) {
            for (int j = 2; j < columns; j++) {
                Tile[] tmp = new Tile[rows];
                for (int i = 0; i < rows; i++) {
                    tmp[i] = shelfSnapshot[i][j];
                }
                if ((Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count()) !=
                        numTilesColumn+ 1) {
                    return false;
                }
                numTilesColumn = Math.toIntExact(Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count());
            }
            return true;
        }


        //checking descending branch
        else if (numTilesColumn ==
                Arrays.stream(firstColumn).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count() - 1) {
            for (int j = 2; j < columns; j++) {
                Tile[] tmp = new Tile[rows];
                for (int i = 0; i < rows; i++) {
                    tmp[i] = shelfSnapshot[i][j];
                }
                if ((Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count()) !=
                        numTilesColumn - 1){
                    return false;
                }
                numTilesColumn = Math.toIntExact(Arrays.stream(tmp).filter(x -> x != Tile.BLANK && x != Tile.NOT_VALID).count());
            }
            return true;
        }

        else return false;
    }
}
