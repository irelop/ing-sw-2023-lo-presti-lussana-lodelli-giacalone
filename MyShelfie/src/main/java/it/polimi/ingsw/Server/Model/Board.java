package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.utils.Exceptions.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


/**
 * Class to manage the board
 */

public class Board implements Serializable {
    protected final static int MAX_ROWS = 9;
    protected final static int MAX_COLUMNS = 9;
    protected final static int MAX_DRAWABLE_COMMON = 2;
    private Bag bag;
    private Tile[][] boardGrid;
    private final CommonGoalCard[] commonGoalCards;

    public Board(){
        bag = new Bag();
        commonGoalCards = new CommonGoalCard[MAX_DRAWABLE_COMMON];
        boardGrid = new Tile[MAX_ROWS][MAX_COLUMNS];
    }
    //- - - - - - - - - - - - - - - -| INIT BOARD |- - - - - - - - - - - - - - - - - - - - - - -
    /**
     * OVERVIEW: this method allows to initialize the grid of a board given a matrix of tiles which
     * can be read by a file.
     * @param matrix: the matrix of tiles chosen for the initialization.
     * @author Matteo Lussana
     */
    public void initFromMatrix(Tile[][] matrix){
        for(int i=0; i<matrix.length; i++)
            for(int j=0; j<matrix[0].length; j++){
                boardGrid[i][j] = matrix[i][j];
            }
    }

    /**
     * OVERVIEW: initialization of the board with a geometric solution:
     * the edges of the board corrisponds at 3 differents parabolas:
     *  - 2 players: y = (1.24)x^2
     *  - 3 players: y = (1.256)x^2 + 0.03
     *  - 4 players: y = (1.27)x^2
     * using these parabolas we can fill the first corner (top right) and then rotate it and fill the entire board
     * @param numPlayers : int
     * @author Matteo Lussana
     */
    public void initGridParabolic(int numPlayers){
        for(int i=0; i<MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLUMNS; j++) {
                boardGrid[i][j] = Tile.NOT_VALID;
            }
        }
        boardGrid[MAX_ROWS/2][MAX_COLUMNS/2] = Tile.BLANK;

        switch(numPlayers){
            case 2: init2PlayersParabolic(); break;
            case 3: init3PlayersParabolic(); break;
            case 4: init4PlayersParabolic(); break;
        }

        //rotate
        for(int i=0; i<(MAX_ROWS/2)+1; i++)
            for(int j=0; j<MAX_COLUMNS/2; j++){
                boardGrid[j][MAX_COLUMNS-1-i]=boardGrid[i][j];
                boardGrid[MAX_ROWS-1-j][i]=boardGrid[i][j];
                boardGrid[MAX_ROWS-1-i][MAX_COLUMNS-1-j]=boardGrid[i][j];
            }
    }

    /**
     * OVERVIEW: initialization of the board for 2 players
     *  @author Matteo Lussana
     */
    private void init2PlayersParabolic(){
        float x;
        for(int i=3; i<MAX_ROWS-1; i++){
            if(i!=3){
                x = (int) Math.pow(1.24,i);
                int j=0;
                while(j<x-1){
                    boardGrid[i-((MAX_ROWS/2)-1)][((MAX_COLUMNS/2)-1)-j] = Tile.BLANK;
                    j++;
                }
            }
        }
    }

    /**
     * OVERVIEW: initialization of the board for 3 players
     *  @author Matteo Lussana
     */
    private void init3PlayersParabolic(){
        int x;
        for(int i=3; i<MAX_ROWS-1; i++){
                x = (int) (0.03 + Math.pow(1.256,i));
                int j=0;
                while(j<x-1){
                    boardGrid[i-((MAX_ROWS/2)-1)][((MAX_COLUMNS/2)-1)-j] = Tile.BLANK;
                    j++;
                }
        }
    }

    /**
     * OVERVIEW: initialization of the board for 4 players
     * @author Matteo Lussana
     */
    private void init4PlayersParabolic(){
        int x;
        for(int i=3; i<MAX_ROWS-1; i++){
                x = (int) Math.pow(1.27,i);
                int j=0;
                while(j<x-1){
                    boardGrid[i-((MAX_ROWS/2)-1)][((MAX_COLUMNS/2)-1)-j] = Tile.BLANK;
                    j++;
                }
        }
    }

    /**
     * OVERVIEW: this method picks the tiles from the board, and it returns them to the player's hand.
     * @see Tile
     * @param initialPositionR : index of the initial row
     * @param initialPositionC : index of the initial column
     * @param numberOfTiles : number of tiles chosen
     * @param direction : direction in which the player chooses the tiles
     * @return ArrayList<Tile> chosenTiles != null : the chosen tiles from the board
     * @author Irene Lo Presti
     */
    public ArrayList<Tile> pickTilesFromBoard(int initialPositionR, int initialPositionC, int numberOfTiles,
                                   char direction){
        ArrayList<Tile> chosenTiles = new ArrayList<>();

        for(int i=0; i<numberOfTiles; i++){
            chosenTiles.add(boardGrid[initialPositionR][initialPositionC]);
            boardGrid[initialPositionR][initialPositionC] = Tile.BLANK;
            if(direction == 'n')
                initialPositionR--;
            else if(direction == 's')
                initialPositionR++;
            else if(direction == 'e')
                initialPositionC++;
            else if(direction == 'w')
                initialPositionC--;
        }

        return chosenTiles;
    }

    /**
     * OVERVIEW: this method checks if the cells are valid and not empty and if the tiles have a free side
     * @param r : int
     * @param c : int
     * @throws InvalidPositionException if the tile has not a free side
     * @throws InvalidCellException if the cell is not valid
     * @throws EmptyCellException if the cell is empty
     * @author Irene Lo Presti
     */
    public void checkPosition(int r, int c) throws InvalidPositionException, InvalidCellException, EmptyCellException, OutOfBoardException {

        if(r<0 || r>=MAX_ROWS || c<0 || c>=MAX_COLUMNS) throw new OutOfBoardException();

        if(boardGrid[r][c] == Tile.NOT_VALID) throw new InvalidCellException();

        if(boardGrid[r][c] == Tile.BLANK) throw new EmptyCellException();

        //if it is on the border a side is free
        if(r == MAX_ROWS-1 || c == MAX_COLUMNS-1 || r==0 || c==0)
            return;

        if(boardGrid[r+1][c].equals(Tile.BLANK) || boardGrid[r-1][c].equals(Tile.BLANK) ||
                boardGrid[r][c+1].equals(Tile.BLANK) || boardGrid[r][c-1].equals(Tile.BLANK))
            return;

        if(boardGrid[r+1][c].equals(Tile.NOT_VALID) || boardGrid[r-1][c].equals(Tile.NOT_VALID) ||
                boardGrid[r][c+1].equals(Tile.NOT_VALID) || boardGrid[r][c-1].equals(Tile.NOT_VALID))
            return;

        throw new InvalidPositionException();

    }

    /**
     * OVERVIEW: this method checks if the cells are valid and not empty, if the tiles have a free side and
     * if the direction is correct
     * @param direction : char
     * @param numberOfTiles : int
     * @param r : int
     * @param c : int
     * @param maxTilesPickable : int
     * @throws InvalidPositionException if the tiles don't have a free side
     * @throws OutOfBoardException if the direction chosen goes out of the board
     * @throws InvalidCellException if tone of the cells is not valid
     * @throws EmptyCellException if one of the cells is empty
     * @throws InvalidDirectionException if the direction is not valid
     * @author Irene Lo Presti
     */
    public void checkDirectionAndNumberOfTiles(char direction, int numberOfTiles, int r, int c,
                                               int maxTilesPickable) throws InvalidPositionException,
            InvalidCellException, EmptyCellException, OutOfBoardException, InvalidNumberOfTilesException,
            InvalidDirectionException {

        if(numberOfTiles==0)
            return;

        if(direction != 'n' && direction != 's' && direction != 'e' && direction != 'w')
            throw new InvalidDirectionException();

        numberOfTiles++; //aumento di uno per avere il totale delle tessere (così conto anche la prima)

        if(numberOfTiles > maxTilesPickable || numberOfTiles < 0)
            throw new InvalidNumberOfTilesException(maxTilesPickable);
        
        switch (direction) {
            case 'e' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (c + i >= MAX_COLUMNS) throw new OutOfBoardException();
                    checkPosition(r, c+i);
                }
            }
            case 'n' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (r - i < 0) throw new OutOfBoardException();
                    checkPosition(r-i, c);
                }
            }
            case 's' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (r + i >= MAX_ROWS) throw new OutOfBoardException();
                    checkPosition(r+i, c);
                }
            }
            case 'w' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (c - i < 0) throw new OutOfBoardException();
                    checkPosition(r, c-i);
                }
            }
        }
    }
    /**
     * OVERVIEW: this method refills the board with tiles from the bag
     * @author Irene Lo Presti
     */
    public boolean refill(){
        for(int r=0; r<MAX_ROWS; r++)
            for(int c=0; c<MAX_COLUMNS; c++)
                if(boardGrid[r][c] == Tile.BLANK)
                    try {
                        boardGrid[r][c] = bag.draw();
                    } catch (EmptyBagException ex){
                        /*ex.toString();
                        ex.printStackTrace();*/
                        for(int i=0; i<MAX_ROWS; i++)
                            for(int j=0; j<MAX_COLUMNS; j++)
                                if(boardGrid[i][j] != Tile.BLANK && boardGrid[i][j] != Tile.NOT_VALID)
                                    return true;
                        return false;
                    }
        return true;
    }

    /**
     * OVERVIEW: this method checks if the board needs to be refilled
     * @return true if the board needs to be refilled, false otherwise
     * @author Irene Lo Presti
     */
    public boolean needRefill(){
        for(int r=0; r<MAX_ROWS-1; r++)
            for(int c=0; c<MAX_COLUMNS-1; c++){
                if(boardGrid[r][c] != Tile.BLANK && boardGrid[r][c]!=Tile.NOT_VALID){
                    if(boardGrid[r+1][c] != Tile.BLANK && boardGrid[r+1][c]!=Tile.NOT_VALID)
                        return false;
                    else if(boardGrid[r][c+1] != Tile.BLANK && boardGrid[r][c+1]!=Tile.NOT_VALID)
                        return false;
                }
            }
        return true;
    }

    /**
     * OVERVIEW: this method asks the index of the row of the initial position
     * WITHOUT SCANNER, for testing
     * @param r : int
     * @return row >=0 || row < MAX_ROWS
     * @throws OutOfBoardException if the chosen row is not between 0 and MAX_ROWS-1
     * @author Irene Lo Presti
     */
    public int getInitialRow(int r) throws OutOfBoardException {
        r = r - 1;
        if(r<0 || r>=MAX_ROWS) throw new OutOfBoardException();
        else return r;
    }

    /**
     * OVERVIEW: this method asks the index of the column of the initial position
     * WITHOUT SCANNER for testing
     * @return row >=0 || row < MAX_COLUMN
     * @throws OutOfBoardException if the chosen column is not between 0 and MAX_ROWS-1
     * @author Irene Lo Presti
     */
    public int getInitialColumn(int c) throws OutOfBoardException {
        c = c - 1;
        if(c<0 || c>=MAX_COLUMNS) throw new OutOfBoardException();
        else return c;
    }


    /**
     * OVERVIEW: this method get the number of tiles
     * NO SCANNER, for test
     * @param maxTilesPickable : int
     * @return numberOfTiles > 0 && numberOfTiles <= maxTilesPickable
     * @throws InvalidNumberOfTilesException if the number of tiles chosen is not between 1 and
     *               the maximum number of tiles pickable
     * @author Irene Lo Presti
     */
    public int getNumberOfTiles(int maxTilesPickable, int numberOfTiles) throws InvalidNumberOfTilesException {
        if(numberOfTiles>maxTilesPickable || numberOfTiles <= 0) throw new InvalidNumberOfTilesException(maxTilesPickable);   // cambia il nome perchè toomany non ha senso
        else return numberOfTiles;
    }


    /**
     * OVERVIEW: this method gets the direction
     * NO SCANNER, for test
     * @return direction
     * @throws InvalidDirectionException if the direction chosen is not n, s, e or w
     * @author Irene Lo Presti
     */
    public char getDirection(char direction) throws InvalidDirectionException{
        if(direction != 'n' && direction != 's' && direction != 'e' && direction != 'w')
            throw new InvalidDirectionException();
        else return direction;

    }

    /**
     * OVERVIEW: a first draft of the initialization of the board. This method initialize the matrix with NOT_VALID
     * tiles
     * @param numPlayers: number of player playing
     * @author Irene Lo Presti
     */
    public void initGrid(int numPlayers){

        for(int r=0; r<MAX_ROWS; r++) {
            for (int c = 0; c < MAX_COLUMNS; c++) {
                boardGrid[r][c] = Tile.NOT_VALID;
            }
        }

        switch (numPlayers){
            case 2:init2Players(); break;

            case 3:init3Players(); break;

            case 4:init4Players(); break;
        }
    }

    /**
     * OVERVIEW: initialization of the board for 2 players
     * @author Irene Lo Presti
     */
    public void init2Players(){
        int ns, e, w, sn, we, ew;
        ns = 1;
        w = (MAX_COLUMNS-1)/2 - 1;
        e = (MAX_COLUMNS-1)/2 + 1;
        sn = MAX_ROWS-2;
        while(ns<=4){
            we = w;
            ew = e;
            while(we<=e){
                if(!(ns==1 && we==5) && !(ns==2 && (we==2 || we==6)) && !(ns==3 && we==1) && !(ns==4 && (we==8 || we==0))){
                    boardGrid[ns][we] = Tile.BLANK;
                    if(sn!=4)
                        boardGrid[sn][ew] = Tile.BLANK;
                }
                we++;
                ew--;
            }
            w--;
            e++;
            ns++;
            sn--;
        }
    }

    /**
     * OVERVIEW: initialization of the board for 3 players
     * @deprecated
     * @author Irene Lo Presti
     */
    private void init3Players() {

        int ns, e, w, sn, we, ew;
        ns = 0;
        w = (MAX_COLUMNS-1)/2 - 1;
        e = (MAX_COLUMNS-1)/2 + 1;
        sn = MAX_ROWS-1;

        boardGrid[ns][w] = Tile.BLANK;
        boardGrid[sn][e] = Tile.BLANK;

        ns++;
        sn--;

        while (ns <= 4) {
            we = w;
            ew = e;
            if (ns == 3) {
                boardGrid[ns][e + 1] = Tile.BLANK;
                boardGrid[sn][w - 1] = Tile.BLANK;
            }
            while (we <= e) {
                if (!(ns == 1 && we == 5) && !(ns == 3 && we == 1) && !(ns == 4 && (we == 8 || we == 0))) {
                    boardGrid[ns][we] = Tile.BLANK;

                    if (sn != 4) {
                        boardGrid[sn][ew] = Tile.BLANK;
                    }
                }
                we++;
                ew--;
            }
            w--;
            e++;
            ns++;
            sn--;
        }
    }

    /**
     * OVERVIEW: initialization of the board for 4 players
     * @deprecated
     * @author Irene Lo Presti
     */
    private void init4Players() {
        int ns, e, w, sn, we, ew;
        ns = 0;
        w = (MAX_COLUMNS-1)/2 - 1;
        e = (MAX_COLUMNS-1)/2 + 1;
        sn = MAX_ROWS-1;

        boardGrid[ns][w] = Tile.BLANK;
        boardGrid[sn][e] = Tile.BLANK;

        w++;
        e--;

        while (ns <= 4) {
            we = w;
            ew = e;
            if (ns == 3) {
                boardGrid[ns][e + 1] = Tile.BLANK;
                boardGrid[sn][w - 1] = Tile.BLANK;
            }
            while (we <= e) {
                boardGrid[ns][we] = Tile.BLANK;
                boardGrid[sn][ew] = Tile.BLANK;
                we++;
                ew--;
            }
            w--;
            e++;
            ns++;
            sn--;
        }
    }
    //- - - - - - - - - - - - - - - -| GETTER METHODS |- - - - - - - - - - - - - - - - - - - -
    public Map<Tile,Integer> getBag(){
        return bag.getBag();
    }
    public CommonGoalCard getCommonGoalCard(int index){
        return commonGoalCards[index];
    }
    public CommonGoalCard[] getCommonGoalCards() {
        return commonGoalCards;
    }
    public Tile[][] getBoardGrid(){
        return boardGrid;
    }

    //- - - - - - - - - - - - - - - -| SETTER METHODS |- - - - - - - - - - - - - - - - - - - -
    public void setBag(Map<Tile,Integer> bag){
        this.bag.setBag(bag);
    }
    public void setCommonGoalCards(CommonGoalCard[] commonCards){
        System.arraycopy(commonCards, 0, commonGoalCards, 0, MAX_DRAWABLE_COMMON);
    }
}
