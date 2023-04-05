package it.polimi.ingsw;

/**
 * Board class: this class manages the board with the singleton pattern. There is only one initialization, then
 * it is only possible to pick a tile or to refill the board if necessary.
 *
 * @authors Andrea Giacalone, Matteo Lussana, Irene Lo Presti
 */

import it.polimi.ingsw.Exceptions.*;
import java.util.ArrayList;
import java.util.Scanner;

//OVERVIEW: Draft della classe BOARD: mancano i metodi per fillare e check di adiacenza
public class Board {
    protected final static int MAX_ROWS = 9;
    protected final static int MAX_COLUMNS = 9;
    protected final static int MAX_DRAWABLE_COMMON = 2;
    private static Board boardInstance;
    private static Bag bag;
    private static Tile[][] boardGrid;
    private static CommonGoalCard[] commonGoalCards;

    /**
     * OVERVIEW: this method returns the singleton instance of the Board class.
     * @return the instacce of the game board.
     */
    public static Board getBoardInstance(){
        if(boardInstance == null){
            boardInstance = new Board();
            boardGrid = new Tile[MAX_ROWS][MAX_COLUMNS];
            bag = new Bag();
            commonGoalCards = new CommonGoalCard[MAX_DRAWABLE_COMMON];
            for(int i=0; i<MAX_DRAWABLE_COMMON;i++){
                commonGoalCards[i] = CommonGoalDeck.drawCommon();
            }
        }

        return boardInstance;
    }

    /**
     * OVERVIEW: this method allows to initialize the grid of a board given a matrix of tiles which can be read by a file.
     * @param matrix: the matrix of tiles chosen for the initialization.
     */
    public void initFromMatrix(Tile[][] matrix){
        for(int i=0; i<matrix.length; i++)
            for(int j=0; j<matrix[0].length; j++){
                this.boardGrid[i][j] = matrix[i][j];
            }
    }

    /**
     * OVERVIEW: getter method
     * @see CommonGoalCard
     * @param index: int
     * @return commonGoalCard in the position 'index'
     */
    public static CommonGoalCard getCommonGoalCard(int index){
        return commonGoalCards[index];
    }

    public static CommonGoalCard[] getCommonGoalCards() {
        return commonGoalCards;
    }

    /**
     * OVERVIEW: getter method
     * @see Tile
     * @return boardGrid (matrix of Tile)
     */
    public static Tile[][] getBoardGrid(){
        return boardGrid;
    }

    /**
     * OVERVIEW: a first draft of the initialization of the board. In this method we use some variables
     * that move in the board: ns (north - south), sn (south-north), w (west), e (est), we (west-est),
     * ew (est-west).
     * @deprecated
     * @param numPlayers : int
     */
    public void initGrid(int numPlayers){

        for(int r=0; r<MAX_ROWS; r++) {
            for (int c = 0; c < MAX_COLUMNS; c++) {
                boardGrid[r][c] = Tile.NOT_VALID;
            }
        }

        int ns, e, w, sn, we;

        ns = 0;
        e = (MAX_COLUMNS-1)/2;
        w = (MAX_COLUMNS-1)/2;
        sn = MAX_ROWS-1;


        while(ns<=4){
            we = w;
            while(we<=e){
                boardGrid[ns][we] = Tile.BLANK;
                boardGrid[sn][we] = Tile.BLANK;
                we++;
            }
            w--;
            e++;
            ns++;
            sn--;
        }

        switch (numPlayers){
            case 2:init2Players();

            case 3:init3Players();

            case 4:init4Players();
        }
    }

    /**
     * OVERVIEW: initialization of the board for 2 players
     * @deprecated
     */
    private void init2Players(){
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
        sn++;

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
     */
    private void init4Players() {
        int ns, e, w, sn, we, ew;
        ns = 1;
        w = (MAX_COLUMNS-1)/2 - 1;
        e = (MAX_COLUMNS-1)/2 + 1;
        sn = MAX_ROWS-2;

        boardGrid[ns][w] = Tile.BLANK;
        boardGrid[sn][e] = Tile.BLANK;

        ns++;
        sn++;

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

    /**
     * OVERVIEW: this method sets the position of the first tile, the number of tiles that the player wants and
     * which direction (north, south, est, west) the player wants to follow in order to pick the other one/s.
     * Then it call the method pickTilesFromBoard to pick the tiles.
     * @param maxTilesPickable : int
     * @return ArrayList<Tile> chosenTiles != null
     */
    public ArrayList<Tile> chooseTilesFromBoard(int maxTilesPickable){
        int initialPositionR, initialPositionC, numberOfTiles;
        char direction;
        ArrayList<Tile> chosenTiles = new ArrayList<>();

        System.out.println("Insert the initial position of the tile: ");
        do{
            try{
                initialPositionR = getInitalRow();
                initialPositionC = getInitialColumn();
                checkPosition(initialPositionR, initialPositionC);
                break;
            }catch(OutOfBoardException | InvalidPositionException | InvalidCellException e){
                System.out.println(e);
            }
        }while(true);

        System.out.println("Insert the number of tiles that you want to pick and the direction " +
                "you want to follow (north n, south s, est e, west w");
        do{
            try{
                numberOfTiles = getNumberOfTiles(maxTilesPickable);
                direction = getDirection();
                checkDirectionAndNumberOfTiles(direction, numberOfTiles, initialPositionR, initialPositionC);
                break;
            }catch(InvalidNumberOfTilesException | InvalidDirectionException | InvalidPositionException
                    | InvalidCellException e){
                System.out.println(e);
            }
        }while(true);

        return pickTilesFromBoard(initialPositionR, initialPositionC, numberOfTiles, direction);
    }

    /**
     * OVERVIEW: this method picks the tiles from the board, and it returns them to the player's hand.
     * @see Tile
     * @param initialPositionR : int
     * @param initialPositionC : int
     * @param numberOfTiles : int
     * @param direction : char
     * @return ArrayList<Tile> chosenTiles != null
     */
    public ArrayList<Tile> pickTilesFromBoard(int initialPositionR, int initialPositionC, int numberOfTiles, char direction){
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
            else
                initialPositionC--;
        }

        return chosenTiles;
    }

    /**
     * OVERVIEW: this method asks the index of the row of the initial position
     * @return row >=0 || row < MAX_ROWS
     * @throws OutOfBoardException e (if the chosen row is not between 0 and MAX_ROWS-1)
     */
    private int getInitalRow() throws OutOfBoardException {
        int r;
        System.out.print("Row: ");
        Scanner scanner = new Scanner(System.in);
        r = scanner.nextInt() - 1;
        if(r<0 || r>=MAX_ROWS) throw new OutOfBoardException();
        else return r;
    }

    /**
     * OVERVIEW: this method asks the index of the column of the initial position,
     * if the chosen column is not between 0 and MAX_ROWS-1
     * @return row >=0 || row < MAX_COLUMN
     * @throws OutOfBoardException e
     */
    private int getInitialColumn() throws OutOfBoardException {
        int c;
        System.out.print("Column: ");
        Scanner scanner = new Scanner(System.in);
        c = scanner.nextInt() - 1;
        if(c<0 || c>=MAX_COLUMNS) throw new OutOfBoardException();
        else return c;
    }

    /**
     * OVERVIEW: this method throws exceptions if the position chosen is not valid, is blank or if the tile has
     * not a free side
     * @param r : int
     * @param c : int
     * @throws InvalidPositionException e
     * @throws InvalidCellException e
     */
    private void checkPosition(int r, int c) throws InvalidPositionException, InvalidCellException {
        if(boardGrid[r+1][c]!=Tile.BLANK && boardGrid[r-1][c]!=Tile.BLANK &&
                boardGrid[r][c+1]!=Tile.BLANK && boardGrid[r][c-1]!=Tile.BLANK &&
                boardGrid[r+1][c]!=Tile.NOT_VALID && boardGrid[r-1][c]!=Tile.NOT_VALID &&
                boardGrid[r][c+1]!=Tile.NOT_VALID && boardGrid[r][c-1]!=Tile.NOT_VALID)
            throw new InvalidPositionException();
        if(boardGrid[r][c] == Tile.NOT_VALID || boardGrid[r][c] == Tile.BLANK) throw new InvalidCellException();
    }

    /**
     * OVERVIEW: this method get the number of tiles and throws an exception if the number of tiles chosen is not between 1 and
     * the maximum number of tiles pickable
     * @param maxTilesPickable : int
     * @return numberOfTiles > 0 && numberOfTiles <= maxTilesPickable
     * @throws InvalidNumberOfTilesException e
     */
    private int getNumberOfTiles(int maxTilesPickable) throws InvalidNumberOfTilesException {
        Scanner scanner = new Scanner(System.in);
        int numberOfTiles;
        numberOfTiles = scanner.nextInt();
        if(numberOfTiles>maxTilesPickable || numberOfTiles <= 0) throw new InvalidNumberOfTilesException(maxTilesPickable);   // cambia il nome perchè toomany non ha senso
        else return numberOfTiles;
    }

    /**
     * OVERVIEW: this method gets the direction and throws an exception if the direction chosen is not n, s, e or w
     * @return direction
     * @throws InvalidDirectionException e
     */
    private char getDirection() throws InvalidDirectionException{
        Scanner scanner = new Scanner(System.in);
        char direction;
        direction = scanner.next().charAt(0);
        if(direction != 'n' && direction != 's' && direction != 'e' && direction != 'w')
            throw new InvalidDirectionException();
        else return direction;

    }

    /**
     * OVERVIEW: this method throws exception if the tiles chosen are not valid, blank, don't have a free side
     * or the direction chosen goes out of the board.
     * @param direction : char
     * @param numberOfTiles : int
     * @param r : int
     * @param c : int
     * @throws InvalidPositionException e
     * @throws InvalidDirectionException e
     * @throws InvalidCellException e
     */
    private void checkDirectionAndNumberOfTiles(char direction, int numberOfTiles, int r, int c)
            throws InvalidPositionException, InvalidDirectionException, InvalidCellException {
        switch (direction) {
            case 'e' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (c + i >= MAX_COLUMNS) throw new InvalidDirectionException();
                    checkPosition(r, c+i);
                }
            }
            case 'n' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (r - i < 0) throw new InvalidDirectionException();
                    checkPosition(r-i, c);
                }
            }
            case 's' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (r + i >= MAX_ROWS) throw new InvalidDirectionException();
                    checkPosition(r+i, c);
                }
            }
            case 'w' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (c - i < 0) throw new InvalidDirectionException();
                    checkPosition(r, c-i);
                }
            }
        }
    }

    /**
     * OVERVIEW: this method refills the board with tiles from the bag
     */
    public void refill(){
        for(int r=0; r<MAX_ROWS; r++)
            for(int c=0; c<MAX_COLUMNS; c++)
                if(boardGrid[r][c] == Tile.BLANK)
                    try {
                        boardGrid[r][c] = bag.draw();
                    } catch (EmptyBagException ex){
                        ex.toString();
                        ex.printStackTrace();
                    }
    }

    /**
     * OVERVIEW: this method checks if the board needs to be refilled
     * @return true if the board needs to be refilled, false otherwise
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
     * OVERVIEW: initialization of the board with a geometric solution:
     * the edges of the board corrisponds at 3 differents parabolas:
     *  - 2 players: y = (1.24)x^2
     *  - 3 players: y = (1.256)x^2 + 0.03
     *  - 4 players: y = (1.27)x^2
     * using these parabolas we can fill the first corner (top right) and then rotate it and fill the entire board
     * @param numPlayers : int
     */
    public void initGridParabolic(int numPlayers){
        int max_col = MAX_COLUMNS, max_raw = MAX_ROWS;
        for(int i=0; i<MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLUMNS; j++) {
                boardGrid[i][j] = null;
            }
        }
        boardGrid[MAX_ROWS/2][MAX_COLUMNS/2] = Tile.BLANK;

        switch(numPlayers){
            case 2: init2PlayesParabolic();
            case 3: init3PlayesParabolic();
            case 4: init4PlayesParabolic();
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
     */
    private void init2PlayesParabolic(){
        int x;
        for(int i=3; i<MAX_ROWS-1; i++){
            if(i!=3){
                x = (int) Math.pow(1.24,i);
                int j=0;
                while(j<x-1){
                    boardGrid[i-(MAX_ROWS/2)-1][(MAX_COLUMNS/2)-1-j] = Tile.BLANK;
                    j++;
                }
            }
        }
    }

    /**
     * OVERVIEW: initialization of the board for 3 players
     */
    private void init3PlayesParabolic(){
        int x;
        for(int i=3; i<MAX_ROWS-1; i++){
            if(i!=3){
                x = (int) (0.03 + Math.pow(1.256,i));
                int j=0;
                while(j<x-1){
                    boardGrid[i-(MAX_ROWS/2)-1][(MAX_COLUMNS/2)-1-j] = Tile.BLANK;
                    j++;
                }
            }
        }
    }

    /**
     * OVERVIEW: initialization of the board for 4 players
     */
    private void init4PlayesParabolic(){
        int x;
        for(int i=3; i<MAX_ROWS-1; i++){
            if(i!=3){
                x = (int) Math.pow(1.27,i);
                int j=0;
                while(j<x-1){
                    boardGrid[i-(MAX_ROWS/2)-1][(MAX_COLUMNS/2)-1-j] = Tile.BLANK;
                    j++;
                }
            }
        }
    }


}
