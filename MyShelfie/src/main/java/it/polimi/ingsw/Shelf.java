package it.polimi.ingsw;

/**
 * Shelf Class: this class manages each player's library, in particular it allows the insertion of new tiles
 * and counts the adjacent tiles of the same color returning the final score
 *
 * @authors Irene Lo Presti, Matteo Lussana
 */

import it.polimi.ingsw.Exceptions.NotEnoughSpaceInChosenColumnException;
import java.util.ArrayList;

public class Shelf {
    private Tile[][] grid;
    private int[][] beenThere;

    /**
     * OVERVIEW: Constructor: it creates grid that is a matrix 6x5 of tiles, that represents the shelf,
     * initializing all its cell to BLANK (because the shelf is initially empty). It also creates
     * beenThere that is a matrix 6x5 of int that used to control the adjacent tiles of the same color.
     *
     * @see Tile
     */
    public Shelf(){
        this.grid = new Tile[6][5];
        //the index (0;0) indicates the first cell top left
        this.beenThere = new int[6][5];

        for(int r=0; r<6; r++)
            for(int c=0; c<5; c++){
                grid[r][c] = Tile.BLANK;
                beenThere[r][c] = 0;
            }
    }

    /**
     * OVERVIEW: this method takes the tiles in the player's hand (lttleHand), that are already in the
     * right order, and puts them in the player's shelf. If the players chooses a column that has not
     * enough free cells, this method throws NotEnoughSpaceInChosenColumnException
     * @param columnIndex
     * @param littleHand
     * @see Player
     * @throws NotEnoughSpaceInChosenColumnException
     */
    public void insert(int columnIndex, ArrayList<Tile> littleHand) throws NotEnoughSpaceInChosenColumnException {
        if(columnFreeSpace(columnIndex) < littleHand.size()) throw new NotEnoughSpaceInChosenColumnException();
        for(int r=5; r>=0; r--){
            if(grid[r][columnIndex]== Tile.BLANK){
                for(int i=0; i<littleHand.size(); i++){ grid[r+i][columnIndex] = littleHand.get(i);}
                break;
            }
        }
    }

    /**
     * OVERVIEW: this methods checks how many adiancent tiles of the same color there are (calling the
     * spotDimention method) and returns the right score
     * @return score >= 0
     */
    public int spotCheck(){
        int score=0;
        int dimension;
        for(int r=0; r<5; r++)
            for(int c=0; c<6; c++){
                if(beenThere[r][c]==0 && !grid[r][c].equals(Tile.BLANK)){
                    dimension = spotDimention(grid[r][c],r,c,0);
                    switch(dimension){
                        case 1: break;
                        case 2: break;
                        case 3: score = score +2;
                            break;
                        case 4: score = score +3;
                            break;
                        case 5: score = score +5;
                            break;
                        default: score = score +8;
                    }
                }
            }
        return score;
    }

    /**
     * OVERVIEW: recursive function to count the adjacent tiles of the same color
     * @param tile
     * @param startRow
     * @param startColumn
     * @param counter
     * @see Tile
     * @return counter >= 0
     */
    //OVERVIEW: conta il numero di celle del colore color adiacenti a partire dalla cella
    // di indice (startRow, startColumn)
    private int spotDimention(Tile tile, int startRow, int startColumn, int counter){
        beenThere[startRow][startColumn] = 1;
        //right
        if(startColumn+1<5 && beenThere[startRow][startColumn+1]!=1 && grid[startRow][startColumn+1].equals(tile))
            counter = 1 + spotDimention(tile, startRow, startColumn+1, counter);

        //left
        if(startColumn-1>=0 && beenThere[startRow][startColumn-1]!=1 && grid[startRow][startColumn-1].equals(tile))
            counter = 1 + spotDimention(tile, startRow, startColumn-1, counter);

        //down
        if(startRow+1<6 && beenThere[startRow+1][startColumn]!=1 && grid[startRow+1][startColumn].equals(tile))
            counter = 1 + spotDimention(tile, startRow+1, startColumn, counter);

        //up
        if(startRow-1>=0 && beenThere[startRow-1][startColumn]!=1 && grid[startRow-1][startColumn].equals(tile))
            counter = 1 + spotDimention(tile, startRow-1, startColumn, counter);

        return counter;
    }

    //OVERVIEW: ritorna il numero di celle libere della colonna di indice columnIndex
    private int columnFreeSpace(int columnIndex){
        int r=0;
        while(grid[r][columnIndex]== Tile.BLANK && r<6)
            r++;
        return r;
    }

    //OVERVIEW: controlla se la libreria è piena
    public boolean isShelfFull(){
        for(int c=0; c<6; c++)
            if(grid[0][c]== Tile.BLANK) return false;
        return true;
    }

    //OVERVIEW: restituisce il numero massimo di tessere pescabili in tutta la shelf
    public int maxTilesPickable(){
        int freeSpace=0;
        int r;
        for(int c=0; c<6; c++) {
            r=0;
            while (r < 5 && grid[r][c] == Tile.BLANK){
                r++;
                if(r==3) return 3;
            }
            if (freeSpace < r) freeSpace = r;
        }
        return freeSpace;
    }
}