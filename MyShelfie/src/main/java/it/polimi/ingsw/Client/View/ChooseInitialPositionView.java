package it.polimi.ingsw.Client.View;
import it.polimi.ingsw.Model.Exceptions.OutOfBoardException;
import it.polimi.ingsw.Server.Messages.*;
import java.util.Scanner;

/**
 * View where the client chooses the initial position of the tiles that he/she wants to choose from the board
 * @author Irene Lo Presti
 */


public class ChooseInitialPositionView extends View{

    int MAX_COLUMNS = 9, MAX_ROWS = 9;

    private PlayerNicknameMsg playerPlayingNicknameMsg;
    private MaxTilesPickableMsg maxTilesPickableMsg;

    /**
     * OVERVIEW: constructor method
     * @param playerPlayingNicknameMsg: message from the server with the nickname of the player
     * @param maxTilesPickableMsg: message from the server with the maximum number of tiles pickable
     *                           from the board
     */
    public ChooseInitialPositionView(PlayerNicknameMsg playerPlayingNicknameMsg, MaxTilesPickableMsg maxTilesPickableMsg){

        this.playerPlayingNicknameMsg = playerPlayingNicknameMsg;
        this.maxTilesPickableMsg = maxTilesPickableMsg;
    }

    /**
     * OVERVIEW: in this method the player chooses the initial position
     */
    @Override
    public void run(){

        View nextView = new ChooseDirectionAndNumberOfTilesView(playerPlayingNicknameMsg, maxTilesPickableMsg);

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
        initialPositionMsg.processMessage(getOwner().getServerHandler());

        if (nextView != null)
            getOwner().transitionToView(nextView);
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
