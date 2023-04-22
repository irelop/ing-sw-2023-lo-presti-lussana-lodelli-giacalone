package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Model.Exceptions.InvalidDirectionException;
import it.polimi.ingsw.Model.Exceptions.InvalidNumberOfTilesException;
import it.polimi.ingsw.Server.Messages.PlayerNicknameMsg;

import java.util.Scanner;

public class ChooseDirectionAndNumberOfTilesView extends View{

    private final PlayerNicknameMsg playerPlayingNicknameMsg;
    MaxTilesPickableMsg maxTilesPickableMsg;

    public ChooseDirectionAndNumberOfTilesView(PlayerNicknameMsg playerPlayingNicknameMsg,
                                               MaxTilesPickableMsg maxTilesPickableMsg){
        this.playerPlayingNicknameMsg = playerPlayingNicknameMsg;
        this.maxTilesPickableMsg = maxTilesPickableMsg;
    }

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

        getOwner().getServerHandler().sendCommandMessage(directionAndNumberOfTilesMsg);

        if (nextView != null)
            getOwner().transitionToView(nextView);
    }

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
