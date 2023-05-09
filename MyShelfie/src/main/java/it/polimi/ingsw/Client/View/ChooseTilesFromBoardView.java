package it.polimi.ingsw.Client.View;

/**
 * VIEW that prints the board, the common goal cards and the personal card of the player.
 * @author Irene Lo Presti
 */

import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidDirectionException;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidNumberOfTilesException;
import it.polimi.ingsw.Server.Model.Exceptions.OutOfBoardException;
import it.polimi.ingsw.Server.Model.Tile;

import java.util.Scanner;

import static it.polimi.ingsw.Client.View.ColorCode.*;

public class ChooseTilesFromBoardView extends View {

    public int MAX_COLUMNS = 9;
    public int MAX_ROWS = 9;

    private final YourTurnMsg yourTurnMsg;
    private InitialPositionAnswer initialPositionAnswer;
    private PlayerChoiceAnswer playerChoiceAnswer;

    /**
     * Constructor method
     * @param yourTurnMsg: message from the server that contains the player's nickname, his/her personal card,
     *                 the board, the common cards, the number of turn and the order in which the players will play.
     */
    public ChooseTilesFromBoardView(YourTurnMsg yourTurnMsg){
        this.yourTurnMsg = yourTurnMsg;
    }

    @Override
    public void run() {

        int r, c, numberOfTiles;
        char direction;
        boolean goOn = false;

        //If it's the first turn, it prints the order in which the players will play
        if(yourTurnMsg.firstTurn)
            printOrderOfPlayers();
        printGoalCardsInfo();
        printShelf();
        printBoard(-1, -1);

        System.out.println();
        System.out.println(yourTurnMsg.nickname + ", it's your turn to pick the tiles from the board!");
        System.out.println(yourTurnMsg.nickname + ", choose the position of the first tile, remember that" +
                " after you choose it, you have to select the number of tiles you want and" +
                " the direction (north, south, east, west) in which" +
                " you want to choose these other tiles");
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

                r--;
                c--;


                InitialPositionMsg initialPositionMsg = new InitialPositionMsg(r, c);
                getOwner().getServerHandler().sendMessageToServer(initialPositionMsg);

                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(this.initialPositionAnswer.answer);
                if (this.initialPositionAnswer.valid)
                    goOn = true;

            } while (!goOn);
        }

        printBoard(r, c);
        System.out.println("The initial position is marked with a star");

        //choosing the number of tiles to pick and the direction
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

                PlayerChoiceMsg playerChoiceMsg = new PlayerChoiceMsg(r, c, direction, numberOfTiles,
                        yourTurnMsg.maxTilesPickable);
                getOwner().getServerHandler().sendMessageToServer(playerChoiceMsg);

                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(this.playerChoiceAnswer.answer);
                if (this.playerChoiceAnswer.valid)
                    goOn = true;

                } while (!goOn);
            }

    }

    public void printShelf(){
        System.out.println(yourTurnMsg.nickname + ", this is your personal shelf:");
        printSmallMatrix(yourTurnMsg.shelfSnapshot);
        System.out.println();
    }

    public void setInitialPositionAnswer(InitialPositionAnswer answer){
        this.initialPositionAnswer = answer;
    }
    public void setPlayerChoiceAnswer(PlayerChoiceAnswer answer){
        this.playerChoiceAnswer = answer;
    }


    /**
     * this method prints the order of the players
     */
    public void printOrderOfPlayers(){
        if(yourTurnMsg.nickname.equals(yourTurnMsg.playersNames.get(0)))
            System.out.println(yourTurnMsg.nickname + ", you have the chair so you are the first one to play!");
        System.out.println("This is the order of playing:");
        for(int i=0; i<yourTurnMsg.playersNames.size(); i++)
            System.out.println((i+1)+") "+yourTurnMsg.playersNames.get(i));
        System.out.println();
    }

    /**
     * this method prints the common cards and the personal card
     */
    public void printGoalCardsInfo(){
        System.out.println("Personal goal card:");
        printSmallMatrix(yourTurnMsg.personalGoalCard.getPattern());
        System.out.println();

        System.out.println("Common goal cards:");
        for(int i=0; i<yourTurnMsg.commonGoalCards.length; i++){
            printSmallMatrix(yourTurnMsg.commonGoalCards[i].getCardInfo().getSchema());
            System.out.println("x"+yourTurnMsg.commonGoalCards[i].getCardInfo().getTimes());
            System.out.println(yourTurnMsg.commonGoalCards[i].getCardInfo().getDescription());
            System.out.println();
        }


    }

    /**
     * This method prints the pattern of the cards
     * @param pattern: matrix of tiles with the pattern to follow in order to achieve the goal (personal or common)
     */
    public void printSmallMatrix(Tile[][] pattern){
        String code = "\u25CF";
        for(int r=0; r<6; r++){

            for(int c=0; c<5; c++){

                switch (pattern[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(BLANK.code + code + RESET.code);
                    case PINK -> System.out.print(PINK.code + code + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + code + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + code + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + code + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    /**
     * This method prints the board
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

                if(r == initialRow && c == initialColumn)
                    code = "*";
                else
                    code = "\u25CF";

                switch (yourTurnMsg.boardSnapshot[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(BLANK.code + code + RESET.code);
                    case PINK -> System.out.print(PINK.code + code + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + code + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + code + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + code + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.println();
        }

    }

    /**
     * Getter method for the initial row
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
     * Getter method for the initial column
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
     * Getter method for the number of tiles
     * @return number of tiles >= 0 && number of tiles < maxTilesPickable-1
     * @throws InvalidNumberOfTilesException if the number of tiles is not between 0 and axTilesPickable-1
     */
    private int getNumberOfTiles() throws InvalidNumberOfTilesException{

        System.out.println(yourTurnMsg.nickname + ", now it's time to" +
                " insert the number of tiles that you " +
                "want to chose (other than the one that you have already chose).");
        System.out.println("Remember: you can chose between 0 (if you don't want to pick others tiles) " +
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

    /**
     * Getter method for direction
     * @return direction (n, s, w or e)
     * @throws InvalidDirectionException if the direction is not n, s, w or e
     */
    private char getDirection() throws InvalidDirectionException {

        System.out.println(yourTurnMsg.nickname + ", now it's time to" +
                " insert the direction in which you want to choose those tiles.");
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

    public void notifyView(){
        synchronized (this) {
            this.notify();
        }
    }

}
