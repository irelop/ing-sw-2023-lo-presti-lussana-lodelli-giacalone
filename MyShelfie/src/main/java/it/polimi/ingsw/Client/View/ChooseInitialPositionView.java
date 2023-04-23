package it.polimi.ingsw.Client.View;
import it.polimi.ingsw.Model.Exceptions.OutOfBoardException;
import it.polimi.ingsw.Server.Messages.*;
import java.util.Scanner;

/**
 * View where the client chooses the initial position of the tiles that he/she wants to choose from the board
 * @author Irene Lo Presti
 */


public class ChooseInitialPositionView extends View{

    public int MAX_COLUMNS = 9;
    public int MAX_ROWS = 9;

    private final PlayerNicknameMsg playerPlayingNicknameMsg;
    private final MaxTilesPickableMsg maxTilesPickableMsg;
    private final BoardMsg boardMsg;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_LIGHTBLUE = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_PINK = "\u001B[35m";
    public static final String ANSI_BLACK = "\u001B[30m";


    /**
     * OVERVIEW: constructor method
     * @param playerPlayingNicknameMsg: message from the server with the nickname of the player
     * @param maxTilesPickableMsg: message from the server with the maximum number of tiles pickable
     *                           from the board
     * @param boardMsg : message from the server with a snapshot of the board
     */
    public ChooseInitialPositionView(PlayerNicknameMsg playerPlayingNicknameMsg,
                                     MaxTilesPickableMsg maxTilesPickableMsg, BoardMsg boardMsg){

        this.playerPlayingNicknameMsg = playerPlayingNicknameMsg;
        this.maxTilesPickableMsg = maxTilesPickableMsg;
        this.boardMsg = boardMsg;
    }

    /**
     * OVERVIEW: in this method the player chooses the initial position
     */
    @Override
    public void run(){

        View nextView;

        printBoard();

        System.out.println(playerPlayingNicknameMsg.nickname + ", it's your turn to pick the tiles from the board!");
        System.out.println(playerPlayingNicknameMsg.nickname + ", choose the position of the first tile, remember that " +
                "after you choose it, you have to select the number of tiles you want and" +
                " the direction (north, south, east, west) in which" +
                "you want to choose these other tiles");
        System.out.println("Remember also that the board is 9x9 and that you have to choose a cell" +
                "with a tile (so it has to be valid and not empty) with a free side");

        int r;
        int c;

        do{
            try{
                r = getInitialRow();
                break;
            }catch(OutOfBoardException e){
                System.out.println(e);
            }
        }while(true);

        do{
            try{
                c = getInitialColumn();
                break;
            }catch(OutOfBoardException e){
                System.out.println(e);
            }
        }while(true);

        InitialPositionMsg initialPositionMsg = new InitialPositionMsg(r, c);
        getOwner().getServerHandler().sendMessageToServer(initialPositionMsg);

        nextView = new ChooseDirectionAndNumberOfTilesView(playerPlayingNicknameMsg, maxTilesPickableMsg, boardMsg, initialPositionMsg);

        if (nextView != null)
            getOwner().transitionToView(nextView);
    }

    /**
     * OVERVIEW: this method prints the board
     */
    public void printBoard(){

        String code = "\u25CF";

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
                switch (boardMsg.boardSnapshot[r][c]) {
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
}
