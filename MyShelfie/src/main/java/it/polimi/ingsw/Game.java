package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private ArrayList<Player> playersConnected;
    private boolean isOver;
    private PersonalGoalDeck personalDeck;
    private CommonGoalDeck commonDeck;
    private Board board;


    public Game(ArrayList<Player> playersConnected){
        //fill the arrayList of players
        this.playersConnected = new ArrayList<Player>();
        this.playersConnected.addAll(playersConnected);

        this.isOver = false;

        this.personalDeck = new PersonalGoalDeck();
        this.commonDeck = new CommonGoalDeck();
        this.board = new Board();
    }

    //OVERVIEW: manage all the moves of a single player during his turn
    private void turn(Player playerPlaying){
        //call  the maxpickable tiles by the player
        int maxTilesPickable = playerPlaying.myShelfie.maxTilesPickable();

        // the player choose the tiles from the board
        ArrayList<Tile> chosenTiles = board.pick(maxTilesPickable);

        //copy the tiles choosen in the player littleHand already in the correct order
        playerPlaying.orderTiles(chosenTiles);

        do{
            try {
                System.out.println("choose the Colomn:");
                Scanner scanner = new Scanner(System.in);
                int columnChosen = scanner.nextInt();

                playerPlaying.myShelfie.insert(columnChosen);
                break;
            } catch (NotEnoughSpaceInChosenColumnException e){
                System.out.println(e);
            }


        }while(true);



    }

    private void manageTurn(){
        while(!isOver){
            for (Player player : playersConnected) turn(player);
        }
    }
}
