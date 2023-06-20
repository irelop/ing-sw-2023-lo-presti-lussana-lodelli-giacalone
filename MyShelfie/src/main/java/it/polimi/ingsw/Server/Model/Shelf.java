package it.polimi.ingsw.Server.Model;

/**
 * Shelf Class: this class manages each player's library, in particular it allows the insertion of new tiles
 * and counts the adjacent tiles of the same color returning the final score
 *
 */

import it.polimi.ingsw.utils.Exceptions.NotEnoughSpaceInChosenColumnException;

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
     * @author Irene Lo Presti, Matteo Lussana
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
     * OVERVIEW: Constructor: it creates grid that is a matrix 6x5 of tiles, that represents the shelf,
     * initializing all its cell to BLANK (because the shelf is initially empty). It also creates
     * beenThere that is a matrix 6x5 of int that used to control the adjacent tiles of the same color.
     *
     * @see Tile
     * @author Irene Lo Presti, Matteo Lussana
     */
    public Shelf(Tile[][] grid){
        //the index (0;0) indicates the first cell top left
        this.beenThere = new int[6][5];
        this.grid = grid;

        for(int r=0; r<6; r++)
            for(int c=0; c<5; c++){
                beenThere[r][c] = 0;
            }
    }

    /**
     * OVERVIEW: this method takes the tiles in the player's hand (lttleHand), that are already in the
     * right order, and puts them in the player's shelf.
     * @param columnIndex : index of the column where the player wants to insert the tiles
     * @param littleHand : ArrayList of the tiles that the player has chosen from the board
     * @see Player
     * @throws NotEnoughSpaceInChosenColumnException If the players chooses a column that has not
     *    enough free cells
     * @author Irene Lo Presti, Matteo Lussana
     */
    public void insert(int columnIndex, ArrayList<Tile> littleHand)
            throws NotEnoughSpaceInChosenColumnException {

        if(columnFreeSpace(columnIndex) < littleHand.size())
            throw new NotEnoughSpaceInChosenColumnException();

        for(int r=5; r>=0; r--){
            if(grid[r][columnIndex]== Tile.BLANK){
                for(int i=0; i<littleHand.size(); i++){ grid[r-i][columnIndex] = littleHand.get(i);}
                break;
            }
        }
    }

    /**
     * OVERVIEW: this method checks how many adjacent tiles of the same color there are (calling the
     * spotDimension method) and returns the right score
     * @return score >= 0
     * @author Matteo Lussana
     */
    public int spotCheck(){
        int score=0;
        int dimension;
        for(int r=0; r<6; r++)
            for(int c=0; c<5; c++){
                if(beenThere[r][c]==0 && !grid[r][c].equals(Tile.BLANK)){
                    dimension = spotDimension(grid[r][c],r,c,1);
                    switch(dimension){
                        case 1:
                        case 2:
                            break;
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
     * @see Tile
     * @param tile : tile to compare with
     * @param startRow : starting row
     * @param startColumn : starting column
     * @param counter : how many tiles of the same color are near
     * @return counter >= 0
     * @author Matteo Lussana
     */
    private int spotDimension(Tile tile, int startRow, int startColumn, int counter){
        beenThere[startRow][startColumn] = 1;

        //right
        if(startColumn+1<5 && beenThere[startRow][startColumn+1]!=1 &&
                grid[startRow][startColumn+1].equals(tile))
            counter = 1 + spotDimension(tile, startRow, startColumn+1, counter);

        //left
        if(startColumn-1>=0 && beenThere[startRow][startColumn-1]!=1 &&
                grid[startRow][startColumn-1].equals(tile))
            counter = 1 + spotDimension(tile, startRow, startColumn-1, counter);

        //down
        if(startRow+1<6 && beenThere[startRow+1][startColumn]!=1 &&
                grid[startRow+1][startColumn].equals(tile))
            counter = 1 + spotDimension(tile, startRow+1, startColumn, counter);

        //up
        if(startRow-1>=0 && beenThere[startRow-1][startColumn]!=1 &&
                grid[startRow-1][startColumn].equals(tile))
            counter = 1 + spotDimension(tile, startRow-1, startColumn, counter);


        return counter;
    }

    /**
     * OVERVIEW: this method counts the number of free cell in the column with the index 'columnIndex'
     * @see Tile
     * @param columnIndex : index of the column chosen
     * @return the maximum number of tiles that the player can choose from the board
     * @author Irene Lo Presti, Matteo Lussana
     */
    private int columnFreeSpace(int columnIndex){
        int r=0;
        while(grid[r][columnIndex]== Tile.BLANK && r<=3)
            r++;
        return r;
    }

    /**
     * OVERVIEW: this method checks if the shelf is completely full
     * @see Tile
     * @return true if the shelf is full, false otherwise
     * @author Irene Lo Presti, Matteo Lussana
     */
    public boolean isShelfFull(){
        for(int c=0; c<5; c++)
            if(grid[0][c] == Tile.BLANK) return false;
        return true;
    }

    /**
     * OVERVIEW: it returns the maximum number of tiles that the player can pick from the board. For
     * example if all the columns have 2 free cells, the player can pick only 2 tiles.
     * @see Tile
     * @return freeSpace >= 1 (not zero because it would mean that the shelf is full)
     * @author Irene Lo Presti, Matteo Lussana
     */
    public int maxTilesPickable(){
        int freeSpace=0;
        int r;
        for(int c=0; c<5; c++) {
            r=0;
            while (r < 6 && grid[r][c] == Tile.BLANK){
                r++;
                if(r==3) return 3;
            }
            if (freeSpace < r) freeSpace = r;
        }
        return freeSpace;
    }

    /**
     * OVERVIEW: getter method
     * @return grid
     * @author Irene Lo Presti, Matteo Lussana
     */
    public Tile[][] getGrid(){
        return grid;
    }

}