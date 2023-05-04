package it.polimi.ingsw.Server.Model;

/**
 * Board class: this class manages the board with the singleton pattern. There is only one initialization, then
 * it is only possible to pick a tile or to refill the board if necessary.
 *
 */

import it.polimi.ingsw.Server.Model.Exceptions.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Board {
    protected final static int MAX_ROWS = 9;
    protected final static int MAX_COLUMNS = 9;
    protected final static int MAX_DRAWABLE_COMMON = 2;
    private static Board boardInstance;
    private static Bag bag;
    private static Tile[][] boardGrid;
    private static CommonGoalCard[] commonGoalCards;

    public Board(){
        bag = new Bag();
        commonGoalCards = new CommonGoalCard[MAX_DRAWABLE_COMMON];
        boardGrid = new Tile[MAX_ROWS][MAX_COLUMNS];
    }

    /**
     * OVERVIEW: this method returns the singleton instance of the Board class.
     * @return the instance of the game board.
     * @author Andrea Giacalone
     */
    public static Board getBoardInstance(){
        if(boardInstance == null){
            boardInstance = new Board();
            /*boardGrid = new Tile[MAX_ROWS][MAX_COLUMNS];
            bag = new Bag();
            commonGoalCards = new CommonGoalCard[MAX_DRAWABLE_COMMON];*/
        }

        return boardInstance;
    }

    /**
     * OVERVIEW: this method allows to initialize the grid of a board given a matrix of tiles which
     * can be read by a file.
     * @param matrix: the matrix of tiles chosen for the initialization.
     * @author Matteo Lussana
     */
    public void initFromMatrix(Tile[][] matrix){
        for(int i=0; i<matrix.length; i++)
            for(int j=0; j<matrix[0].length; j++){
                this.boardGrid[i][j] = matrix[i][j];
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
        int max_col = MAX_COLUMNS, max_raw = MAX_ROWS;
        for(int i=0; i<MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLUMNS; j++) {
                boardGrid[i][j] = null;
            }
        }
        boardGrid[MAX_ROWS/2][MAX_COLUMNS/2] = Tile.BLANK;

        switch(numPlayers){
            case 2: init2PlayesParabolic(); break;
            case 3: init3PlayesParabolic(); break;
            case 4: init4PlayesParabolic(); break;
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
    private void init2PlayesParabolic(){
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
    private void init3PlayesParabolic(){
        int x;
        for(int i=3; i<MAX_ROWS-1; i++){
            if(i!=3){
                x = (int) (0.03 + Math.pow(1.256,i));
                int j=0;
                while(j<x-1){
                    boardGrid[i-((MAX_ROWS/2)-1)][((MAX_COLUMNS/2)-1)-j] = Tile.BLANK;
                    j++;
                }
            }
        }
    }

    /**
     * OVERVIEW: initialization of the board for 4 players
     * @author Matteo Lussana
     */
    private void init4PlayesParabolic(){
        int x;
        for(int i=3; i<MAX_ROWS-1; i++){
            if(i!=3){
                x = (int) Math.pow(1.27,i);
                int j=0;
                while(j<x-1){
                    boardGrid[i-((MAX_ROWS/2)-1)][((MAX_COLUMNS/2)-1)-j] = Tile.BLANK;
                    j++;
                }
            }
        }
    }

    /**
     * OVERVIEW: in the class Game there is the common deck where there is the draw of the two common
     * cards. This method saves the common cards in board.
     * @see CommonGoalCard
     * @param commonCards: the two drawn commonGoalCards
     * @author Irene Lo Presti, Matteo Lussana
     */
    public void setCommonGoalCards(CommonGoalCard[] commonCards){
        System.arraycopy(commonCards, 0, commonGoalCards, 0, MAX_DRAWABLE_COMMON);
    }

    /**
     * OVERVIEW: getter method
     * @see CommonGoalCard
     * @param index: indicates which commonGoalCard is (the number 0 or the number 1)
     * @return commonGoalCard in the position 'index'
     * @author Irene Lo Presti
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
     * @author Irene Lo Presti
     */
    public static Tile[][] getBoardGrid(){
        return boardGrid;
    }

    /**
     * OVERVIEW: this method sets the position of the first tile, the number of tiles that the player wants and
     * which direction (north, south, est, west) the player wants to follow in order to pick the other one/s.
     * Then it call the method pickTilesFromBoard to pick the tiles.
     * @deprecated
     * @param maxTilesPickable: the maximum number of tiles that the player can pick
     * @return ArrayList<Tile> chosenTiles != null
     * @author Irene Lo Presti
     */

    // Ho tolto i commenti che circondavano il metodo perchè sennò ogni volta che
    // scrivevamo Board usciva la scritta rigata. Tanto 90/100 sto metodo andrà cancellato
    // (=> non guardate gli errori causati da questo). Cordiali saluti, Riccardo KEKW

    public void chooseTilesFromBoard(int maxTilesPickable){
        int initialPositionR, initialPositionC, numberOfTiles;
        char direction;
        ArrayList<Tile> chosenTiles = new ArrayList<>();

        System.out.println("Insert the initial position of the tile: ");
        do{
            try{
                initialPositionR = getInitialRow();
                initialPositionC = getInitialColumn();
                checkPosition(initialPositionR, initialPositionC);
                break;
            }catch(OutOfBoardException | InvalidPositionException | InvalidCellException | EmptyCellException e){
                System.out.println(e);
            }
        }while(true);

        //One tile is chosen so the maximum number of tiles must be reduced
        maxTilesPickable--;

        System.out.println("Insert the number of tiles that you want to pick (other than the first)" +
                " and the direction you want to follow (north n, south s, east e, west w");
        do{
            try{
                numberOfTiles = getNumberOfTiles(maxTilesPickable);
                if(numberOfTiles == 0){
                    direction = '0';
                    break;
                }
                direction = getDirection();
                checkDirectionAndNumberOfTiles(direction, numberOfTiles, initialPositionR, initialPositionC, maxTilesPickable);
                break;
            }catch(InvalidNumberOfTilesException | InvalidDirectionException | InvalidPositionException
                   | InvalidCellException | EmptyCellException | OutOfBoardException e){
                System.out.println(e);
            }
        }while(true);

        //return pickTilesFromBoard(initialPositionR, initialPositionC, numberOfTiles+1, direction);
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
    public void pickTilesFromBoard(int initialPositionR, int initialPositionC, int numberOfTiles, char direction, Player player){
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

        player.setLittleHand(chosenTiles);
    }

    /**
     * OVERVIEW: this method asks the index of the row of the initial position
     * WITH SCANNER FROM INPUT
     * @deprecated
     * @return row >=0 || row < MAX_ROWS
     * @throws OutOfBoardException if the chosen row is not between 0 and MAX_ROWS-1
     * @author Irene Lo Presti
     */
    public int getInitialRow() throws OutOfBoardException {
        int r;
        System.out.print("Row: ");
        Scanner scanner = new Scanner(System.in);
        r = scanner.nextInt() - 1;
        if(r<0 || r>=MAX_ROWS) throw new OutOfBoardException();
        else return r;
    }
    /**
     * OVERVIEW: this method asks the index of the row of the initial position
     * WITHOUT SCANNER, now we used it for testing
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
     * OVERVIEW: this method asks the index of the column of the initial position,
     * WITH SCANNER
     * @deprecated
     * @return row >=0 || row < MAX_COLUMN
     * @throws OutOfBoardException if the chosen column is not between 0 and MAX_ROWS-1
     * @author Irene Lo Presti
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
     * OVERVIEW: this method asks the index of the column of the initial position
     * WITHOUT SCANNER, now we used it for testing
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

        //if it is on the boarder a side is free
        if(r == MAX_ROWS-1 || c == MAX_COLUMNS-1 || r==0 || c==0)
            return;

        if(boardGrid[r+1][c]!=Tile.BLANK && boardGrid[r-1][c]!=Tile.BLANK &&
                boardGrid[r][c+1]!=Tile.BLANK && boardGrid[r][c-1]!=Tile.BLANK &&
                boardGrid[r+1][c]!=Tile.NOT_VALID && boardGrid[r-1][c]!=Tile.NOT_VALID &&
                boardGrid[r][c+1]!=Tile.NOT_VALID && boardGrid[r][c-1]!=Tile.NOT_VALID)
            throw new InvalidPositionException();

    }

    /**
     * OVERVIEW: this method get the number of tiles
     * SCANNER
     * @deprecated
     * @param maxTilesPickable : int
     * @return numberOfTiles > 0 && numberOfTiles <= maxTilesPickable
     * @throws InvalidNumberOfTilesException if the number of tiles chosen is not between 1 and
     *              the maximum number of tiles pickable
     * @author Irene Lo Presti
     */
    private int getNumberOfTiles(int maxTilesPickable) throws InvalidNumberOfTilesException {
        Scanner scanner = new Scanner(System.in);
        int numberOfTiles;
        numberOfTiles = scanner.nextInt();
        if(numberOfTiles>maxTilesPickable || numberOfTiles < 0)
            throw new InvalidNumberOfTilesException(maxTilesPickable);   // cambia il nome perchè toomany non ha senso
        else return numberOfTiles;
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
        if(numberOfTiles>maxTilesPickable || numberOfTiles < 0) throw new InvalidNumberOfTilesException(maxTilesPickable);   // cambia il nome perchè toomany non ha senso
        else return numberOfTiles;
    }

    /**
     * OVERVIEW: this method gets the direction
     * SCANNER
     * @deprecated
     * @return direction
     * @throws InvalidDirectionException  if the direction chosen is not n, s, e or w
     * @author Irene Lo Presti
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

        if(direction != 'n' && direction != 's' && direction != 'e' && direction != 'w')
            throw new InvalidDirectionException();

        if(numberOfTiles > maxTilesPickable || numberOfTiles < 0)
            throw new InvalidNumberOfTilesException(maxTilesPickable);

        numberOfTiles++; //aumento di uno per avere il totale delle tessere (così conto anche la prima)

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
     * OVERVIEW: a first draft of the initialization of the board. In this method we use some variables
     * that move in the board: ns (north - south), sn (south-north), w (west), e (est), we (west-est),
     * ew (est-west).
     * @param numPlayers: number of player playing
     * @author Irene Lo Presti
     */
    public void initGrid(int numPlayers){

        System.out.println(numPlayers);
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
            case 2:init2Players(); break;

            case 3:init3Players(); break;

            case 4:init4Players(); break;
        }
    }

    /**
     * OVERVIEW: initialization of the board for 2 players
     * @author Irene Lo Presti
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
     * @author Irene Lo Presti
     */
    private void init4Players() {
        int ns, e, w, sn, we, ew;
        ns = 0;
        w = (MAX_COLUMNS-1)/2;
        e = (MAX_COLUMNS-1)/2;
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

}
