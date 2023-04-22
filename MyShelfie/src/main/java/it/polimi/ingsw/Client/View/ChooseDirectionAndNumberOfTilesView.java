package it.polimi.ingsw.Client.View;
import it.polimi.ingsw.Model.Exceptions.InvalidDirectionException;
import it.polimi.ingsw.Model.Exceptions.InvalidNumberOfTilesException;
import it.polimi.ingsw.Server.Messages.DirectionAndNumberOfTilesMsg;
import it.polimi.ingsw.Server.Messages.MaxTilesPickableMsg;
import it.polimi.ingsw.Server.Messages.PlayerNicknameMsg;
import java.util.Scanner;

/**
 * View where the client chooses the initial position of the tiles that he/she wants to choose from the board
 * @author Irene Lo Presti
 */

public class ChooseDirectionAndNumberOfTilesView extends View{

    private final PlayerNicknameMsg playerPlayingNicknameMsg;
    MaxTilesPickableMsg maxTilesPickableMsg;

    /**
     * OVERVIEW: constructor method
     * @param playerPlayingNicknameMsg: message from the server with the nickname of the player
     * @param maxTilesPickableMsg: message from the server with the maximum number of tiles pickable
     *                           from the board
     */
    public ChooseDirectionAndNumberOfTilesView(PlayerNicknameMsg playerPlayingNicknameMsg,
                                               MaxTilesPickableMsg maxTilesPickableMsg){
        this.playerPlayingNicknameMsg = playerPlayingNicknameMsg;
        this.maxTilesPickableMsg = maxTilesPickableMsg;
    }

    /**
     * OVERVIEW: in this method the player chooses the numberOfTiles and the direction
     */
    @Override
    public void run(){

        View nextView = new InsertInShelfView();
        int numberOfTiles;
        char direction;

        do{
            try{
                numberOfTiles = getNumberOfTiles();
                break;
            }catch(InvalidNumberOfTilesException e){
                System.out.println(e);
            }
        }while(true);

        if(numberOfTiles==0)
            direction = '0';
        else{
            do{
                try{
                    direction = getDirection();
                    break;
                }catch(InvalidDirectionException e){
                    System.out.println(e);
                }
            }while(true);
        }

        DirectionAndNumberOfTilesMsg directionAndNumberOfTilesMsg = new DirectionAndNumberOfTilesMsg(direction, numberOfTiles);
        directionAndNumberOfTilesMsg.processMessage(getOwner().getServerHandler());

        if (nextView != null)
            getOwner().transitionToView(nextView);
    }

    /**
     * OVERVIEW: getter method for direction
     * @return direction (n, s, w or e)
     * @throws InvalidDirectionException if the direction is not n, s, w or e
     */
    private char getDirection() throws InvalidDirectionException {

        System.out.println(playerPlayingNicknameMsg.nickname + ", now it's time to" +
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

        System.out.println(playerPlayingNicknameMsg.nickname + ", now it's time to" +
                " insert the number of tiles that you " +
                "want to chose (other than the one that you have already chose).");
        System.out.println("Remember: you can chose between 0 (if you don't want to pick others tiles)" +
                "and "+(maxTilesPickableMsg.maxTilesPickable-1)+".");
        System.out.print("Please insert the number of tiles: ");

        Scanner scanner = new Scanner(System.in);
        int numberOfTiles;
        numberOfTiles = scanner.nextInt();

        if(numberOfTiles<0 || numberOfTiles>maxTilesPickableMsg.maxTilesPickable-1)
            throw new InvalidNumberOfTilesException(maxTilesPickableMsg.maxTilesPickable);

        else
            return numberOfTiles;
    }


}
