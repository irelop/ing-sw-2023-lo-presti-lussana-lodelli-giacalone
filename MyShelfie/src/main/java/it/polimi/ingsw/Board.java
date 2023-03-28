package it.polimi.ingsw;

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
            for(int i=0; i<MAX_DRAWABLE_COMMON;i++){
                commonGoalCards[i] = CommonGoalDeck.drawCommon();
            }
        }

        return boardInstance;
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



}
