package it.polimi.ingsw;

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

    public static CommonGoalCard getCommonGoalCard(int index){
        return commonGoalCards[index];
    }

    public static CommonGoalCard[] getCommonGoalCards() {
        return commonGoalCards;
    }

    public void initGrid(int numPlayers){
        int r, c, l, x, f, y;

        for(r=0; r<MAX_ROWS; r++) {
            for (c = 0; c < MAX_COLUMNS; c++) {
                boardGrid[r][c] = null;
            }
        }


        boardGrid[0][4] = Tile.BLANK;
        boardGrid[8][4] = Tile.BLANK;

        r = 1;
        c = 3;
        l = 5;
        f = 7;


        while(r<=4){
            x = c;
            while(x<=l){
                boardGrid[r][x] = Tile.BLANK;
                boardGrid[f][x] = Tile.BLANK;
                x++;
            }
            c--;
            l++;
            r++;
            f--;
        }

        switch (numPlayers){
            case 2:init2Players();

            case 3:init3Players();

            case 4:init4Players();
        }
    }

    private void init2Players(){
        int r, c, l, f, x, y;
        r = 1;
        c = 3;
        l = 5;
        f = 7;
        while(r<=4){
            x = c;
            y = l;
            while(x<=l){
                if(!(r==1 && x==5) && !(r==2 && (x==2 || x==6)) && !(r==3 && x==1) && !(r==4 && (x==8 || x==0))){
                    boardGrid[r][x] = Tile.BLANK;
                    if(f!=4)
                        boardGrid[f][y] = Tile.BLANK;
                }
                x++;
                y--;
            }
            c--;
            l++;
            r++;
            f--;
        }
    }

    private void init3Players() {
        boardGrid[0][3] = Tile.BLANK;
        boardGrid[8][5] = Tile.BLANK;

        int r, c, l, f, x, y;

        r = 1;
        c = 3;
        l = 5;
        f = 7;
        while (r <= 4) {
            x = c;
            y = l;
            if (r == 3) {
                boardGrid[r][l + 1] = Tile.BLANK;
                boardGrid[f][c - 1] = Tile.BLANK;
            }
            while (x <= l) {
                if (!(r == 1 && x == 5) && !(r == 3 && x == 1) && !(r == 4 && (x == 8 || x == 0))) {
                    boardGrid[r][x] = Tile.BLANK;

                    if (f != 4) {
                        boardGrid[f][y] = Tile.BLANK;
                    }
                }
                x++;
                y--;
            }
            c--;
            l++;
            r++;
            f--;
        }
    }

    private void init4Players() {
        int r, c, l, f, x, y;
        boardGrid[0][3] = Tile.BLANK;
        boardGrid[8][5] = Tile.BLANK;
        r = 0;
        c = 4;
        l = 4;
        f = 8;
        while (r <= 4) {
            x = c;
            y = l;
            if (r == 3) {
                boardGrid[r][l + 1] = Tile.BLANK;
                boardGrid[f][c - 1] = Tile.BLANK;
            }
            while (x <= l) {
                boardGrid[r][x] = Tile.BLANK;
                boardGrid[f][y] = Tile.BLANK;
                x++;
                y--;
            }
            c--;
            l++;
            r++;
            f--;
        }
    }



    public ArrayList<Tile> pick(int maxTilesPickable){
        int initalPositionR, initialPositionC, numberOfTiles;
        char direction;
        ArrayList<Tile> chosenTiles = new ArrayList<>();

        System.out.println("Insert the initial position of the tile: ");
        do{
            try{
                initalPositionR = getInitalRow();
                initialPositionC = getInitialColumn();
                checkPosition(initalPositionR, initialPositionC);
                break;
            }catch(OutOfBoardException | InvalidPositionException e){
                System.out.println(e);
            }
        }while(true);

        System.out.println("Insert the number of tiles that you want to pick and the direction " +
                "you want to follow (north n, south s, est e, west w");
        do{
            try{
                numberOfTiles = getNumberOfTiles(maxTilesPickable);
                direction = getDirection();
                checkDirectionAndNumberOfTiles(direction, numberOfTiles, initalPositionR, initialPositionC);
                break;
            }catch(InvalidNumberOfTilesException | InvalidDirectionException | InvalidPositionException e){
                System.out.println(e);
            }
        }while(true);

        for(int i=0; i<numberOfTiles; i++){
            chosenTiles.add(boardGrid[initalPositionR][initialPositionC]);
            boardGrid[initalPositionR][initialPositionC] = Tile.BLANK;
            if(direction == 'n')
                initalPositionR--;
            else if(direction == 's')
                initalPositionR++;
            else if(direction == 'e')
                initialPositionC++;
            else
                initialPositionC--;
        }

        return chosenTiles;
    }
    private int getInitalRow() throws OutOfBoardException {
        int r;
        System.out.print("Row: ");
        Scanner scanner = new Scanner(System.in);
        r = scanner.nextInt() - 1;
        if(r<0 || r>=MAX_ROWS) throw new OutOfBoardException();
        else return r;
    }
    private int getInitialColumn() throws OutOfBoardException {
        int c;
        System.out.print("Column: ");
        Scanner scanner = new Scanner(System.in);
        c = scanner.nextInt() - 1;
        if(c<0 || c>=MAX_COLUMNS) throw new OutOfBoardException();
        else return c;
    }

    private void checkPosition(int r, int c) throws InvalidPositionException{
        if(boardGrid[r+1][c]!=Tile.BLANK && boardGrid[r-1][c]!=Tile.BLANK &&
                boardGrid[r][c+1]!=Tile.BLANK && boardGrid[r][c-1]!=Tile.BLANK)
            throw new InvalidPositionException();
    }

    private int getNumberOfTiles(int maxTilesPickable) throws InvalidNumberOfTilesException {
        Scanner scanner = new Scanner(System.in);
        int numberOfTiles;
        numberOfTiles = scanner.nextInt();
        if(numberOfTiles>maxTilesPickable || numberOfTiles <= 0) throw new InvalidNumberOfTilesException(maxTilesPickable);   // cambia il nome perchè toomany non ha senso
        else return numberOfTiles;
    }

    private char getDirection() throws InvalidDirectionException{
        Scanner scanner = new Scanner(System.in);
        char direction;
        direction = scanner.next().charAt(0);
        if(direction != 'n' && direction != 's' && direction != 'e' && direction != 'w')
            throw new InvalidDirectionException();
        else return direction;

    }
    private void checkDirectionAndNumberOfTiles(char direction, int numberOfTiles, int r, int c) throws InvalidPositionException, InvalidDirectionException {
        switch (direction) {
            case 'e' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (r + i > MAX_ROWS) throw new InvalidDirectionException();
                    checkPosition(r + i, c);
                }
            }
            case 'n' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (c - i < 0) throw new InvalidDirectionException();
                    checkPosition(r, c - i);
                }
            }
            case 's' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (c + i > MAX_COLUMNS) throw new InvalidDirectionException();
                    checkPosition(r, c + i);
                }
            }
            case 'w' -> {
                for (int i = 1; i < numberOfTiles; i++) {
                    if (r - i < 0) throw new InvalidDirectionException();
                    checkPosition(r - i, c);
                }
            }
        }
    }

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

        for(int i=0; i<(MAX_ROWS/2)+1; i++)
            for(int j=0; j<MAX_COLUMNS/2; j++){
                boardGrid[j][MAX_COLUMNS-1-i]=boardGrid[i][j];
                boardGrid[MAX_ROWS-1-j][i]=boardGrid[i][j];
                boardGrid[MAX_ROWS-1-i][MAX_COLUMNS-1-j]=boardGrid[i][j];
            }
    }

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
