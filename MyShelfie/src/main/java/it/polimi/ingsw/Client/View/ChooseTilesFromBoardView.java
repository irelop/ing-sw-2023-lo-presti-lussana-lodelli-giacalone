package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.PlayerChoiceMsg;
import it.polimi.ingsw.Server.Messages.InitialPositionMsg;
import it.polimi.ingsw.Server.Messages.YourTurnMsg;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidDirectionException;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidNumberOfTilesException;
import it.polimi.ingsw.Server.Model.Exceptions.OutOfBoardException;

import java.util.Scanner;

public class ChooseTilesFromBoardView extends View {

    public int MAX_COLUMNS = 9;
    public int MAX_ROWS = 9;
    private final YourTurnMsg yourTurnMsg;


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_LIGHTBLUE = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_PINK = "\u001B[35m";
    public static final String ANSI_BLACK = "\u001B[30m";


    public ChooseTilesFromBoardView(YourTurnMsg yourTurnMsg){
        this.yourTurnMsg = yourTurnMsg;
    }

    @Override
    public void run() {

        int r, c, numberOfTiles;
        char direction;
        boolean goOn = false;
        //Object lock = new Object();

        printBoard(-1, -1);

        System.out.println(yourTurnMsg.nickname + ", it's your turn to pick the tiles from the board!");
        System.out.println(yourTurnMsg.nickname + ", choose the position of the first tile, remember that " +
                "after you choose it, you have to select the number of tiles you want and" +
                " the direction (north, south, east, west) in which" +
                "you want to choose these other tiles");
        System.out.println("Remember also that the board is 9x9 and that you have to choose a cell" +
                "with a tile (so it has to be valid and not empty) with a free side");

        //choosing initial row and initial column
        synchronized (this) {
            do {
                // client side exception management
                do {
                    try {
                        r = getInitialRow();
                        break;
                    } catch (OutOfBoardException e) {
                        System.out.println(e);
                    }
                } while (true);

                // client side exception management
                do {
                    try {
                        c = getInitialColumn();
                        break;
                    } catch (OutOfBoardException e) {
                        System.out.println(e);
                    }
                } while (true);

                InitialPositionMsg initialPositionMsg = new InitialPositionMsg(r, c);
                getOwner().getServerHandler().sendMessageToServer(initialPositionMsg);
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(initialPositionMsg.initialPositionAnswer.answer);
                if (initialPositionMsg.initialPositionAnswer.valid)
                    goOn = true;

            } while (!goOn);
        }

            printBoard(r, c);
            System.out.println("The initial position is marked with a star");


        synchronized (this) {
            goOn = false;
            do {
                do {
                        try {
                            numberOfTiles = getNumberOfTiles();
                            break;
                        } catch (InvalidNumberOfTilesException e) {
                            System.out.println(e);
                        }
                    } while (true);

                if (numberOfTiles == 0)
                        direction = '0';
                else {
                    do {
                            try {
                                direction = getDirection();
                                break;
                            } catch (InvalidDirectionException e) {
                                System.out.println(e);
                            }
                    } while (true);
                }

                PlayerChoiceMsg playerChoiceMsg = new PlayerChoiceMsg(r, c, direction, numberOfTiles, yourTurnMsg.maxTilesPickable);
                getOwner().getServerHandler().sendMessageToServer(playerChoiceMsg);

                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(playerChoiceMsg.playerChoiceAnswer.answer);
                if (playerChoiceMsg.playerChoiceAnswer.valid)
                    goOn = true;

                } while (goOn);
            }


            // transition to next view ??

    }
    /**
     * OVERVIEW: this method prints the board
     */
    public void printBoard(int initialRow, int initialColumn){

        String code;

        //printing the indexes of the columns
        System.out.print("\u2716\t");
        for(int i=0; i<MAX_COLUMNS; i++)
            System.out.print((i+1)+"\t");
        System.out.println();

        for(int r=0; r<MAX_ROWS; r++){
            //printing the indexes of the rows
            System.out.print((r+1)+"\t");

            //printing the tiles
            for(int c=0; c<MAX_COLUMNS; c++){

                if(r == initialRow-1 && c == initialColumn-1)
                    code = "*";
                else
                    code = "\u25CF";

                switch (yourTurnMsg.boardSnapshot[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(ANSI_BLACK + code + ANSI_RESET);
                    case PINK -> System.out.print(ANSI_PINK + code + ANSI_RESET);
                    case GREEN -> System.out.print(ANSI_GREEN + code + ANSI_RESET);
                    case BLUE -> System.out.print(ANSI_BLUE + code + ANSI_RESET);
                    case LIGHTBLUE -> System.out.print(ANSI_LIGHTBLUE + code + ANSI_RESET);
                    case WHITE -> System.out.print(ANSI_WHITE + code + ANSI_RESET);
                    case YELLOW -> System.out.print(ANSI_YELLOW + code + ANSI_RESET);
                }
                System.out.print("\t");
            }
            System.out.println();
        }

    }

    /**
     * OVERVIEW: getter method for the initial row
     * @return row>=1 && row<MAX_ROWS
     * @throws OutOfBoardException if the row is not between 1 and MAX_ROWS
     */
    private int getInitialRow() throws OutOfBoardException {
        int r;
        System.out.print("Please insert the row of the initial position: ");
        Scanner scanner = new Scanner(System.in);
        r = scanner.nextInt();
        if(r<=0 || r>MAX_ROWS) throw new OutOfBoardException();
        return r;
    }

    /**
     * OVERVIEW: getter method for the initial column
     * @return column>=1 && column<MAX_ROW
     * @throws OutOfBoardException if the column is not between 1 and MAX_COLUMNS
     */
    private int getInitialColumn() throws OutOfBoardException{
        int c;
        System.out.print("Please insert the column of the initial position: ");
        Scanner scanner = new Scanner(System.in);
        c = scanner.nextInt();
        if(c<=0 || c>MAX_COLUMNS) throw new OutOfBoardException();
        else return c;
    }

    /**
     * OVERVIEW: getter method for direction
     * @return direction (n, s, w or e)
     * @throws InvalidDirectionException if the direction is not n, s, w or e
     */
    private char getDirection() throws InvalidDirectionException {

        System.out.println(yourTurnMsg.nickname + ", now it's time to" +
                " insert the direction that you in which you want to choose those tiles.");
        System.out.println("Remember: you can chose between n (north), s (south), e (east), w (west).");
        System.out.print("Please insert the direction: ");

        Scanner scanner = new Scanner(System.in);
        char direction;
        direction = scanner.next().charAt(0);

        if(direction != 'n' && direction != 's' && direction != 'e' && direction != 'w')
            throw new InvalidDirectionException();

        else
            return direction;
    }

    /**
     * OVERVIEW: getter method for the number of tiles
     * @return number of tiles >= 0 && number of tiles < maxTilesPickable-1
     * @throws InvalidNumberOfTilesException if the number of tiles is not between 0 and axTilesPickable-1
     */
    private int getNumberOfTiles() throws InvalidNumberOfTilesException{

        System.out.println(yourTurnMsg.nickname + ", now it's time to" +
                " insert the number of tiles that you " +
                "want to chose (other than the one that you have already chose).");
        System.out.println("Remember: you can chose between 0 (if you don't want to pick others tiles)" +
                "and "+(yourTurnMsg.maxTilesPickable-1)+".");
        System.out.print("Please insert the number of tiles: ");

        Scanner scanner = new Scanner(System.in);
        int numberOfTiles;
        numberOfTiles = scanner.nextInt();

        if(numberOfTiles<0 || numberOfTiles>yourTurnMsg.maxTilesPickable-1)
            throw new InvalidNumberOfTilesException(yourTurnMsg.maxTilesPickable);

        else
            return numberOfTiles;
    }

}
