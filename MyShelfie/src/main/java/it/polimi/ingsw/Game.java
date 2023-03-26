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

        //checking goals and adding score if necessary
        playerPlaying.score.addScore(playerPlaying.personalGoalCard.checkPersonalGoal(playerPlaying.myShelfie));
        if(!playerPlaying.isCommonGoalAchived(0))
            playerPlaying.score.addScore(Board.commonGoalCards[0].checkPattern());
        if(!playerPlaying.isCommonGoalAchived(1))
            playerPlaying.score.addScore(Board.commonGoalCards[1].checkPattern());

        //checking if a player's shelf is full,
        // if true add +1pt and set the last lap
        if(playerPlaying.myShelfie.isFull()) {
            playerPlaying.score.addScore(1);
            this.isOver = true;
        }
    }

    private void manageTurn(){
        while(!isOver){
            for (Player player : playersConnected) {
                //a player can plays his turn if the match is not over
                // or if that is the last lap
                if(!isOver || (isOver && !player.hasChar()))
                    turn(player);
            }
        }
        for (Player player : playersConnected) {
            int spotScore = player.myShelfie.spotCheck();
            player.score.addScore(spotScore);
        }
        endGame();
    }

    private void endGame(){
        System.out.println("GAME OVER");
        int Player temp;
        for (int i=0; i< playersConnected.size(); i++)
            for (int j=0; j< playersConnected.size(); j++){
                if(playersConnected.get(j).score < playersConnected.get(j+1).score){
                    temp = playersConnected.get(j);
                    playersConnected.get(j) = playersConnected.get(j+1);
                    playersConnected.get(j+1) = temp;
                }
            }
        for(int i=0; i< playersConnected.size(); i++){
            System.out.print("Position "+(i+1));
            System.out.println(playersConnected.get(i));
        }
    }
}
