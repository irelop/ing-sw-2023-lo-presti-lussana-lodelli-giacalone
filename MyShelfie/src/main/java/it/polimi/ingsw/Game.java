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

                playerPlaying.myShelfie.insert(columnChosen, playerPlaying.getLittleHand());
                break;
            } catch (NotEnoughSpaceInChosenColumnException e){
                System.out.println(e);
            }
        }while(true);

        //checking goals and adding score if necessary
        playerPlaying.myScore.addScore(playerPlaying.personalGoalCard.checkPersonalGoal(playerPlaying.myShelfie));
        if(!playerPlaying.isCommonGoalAchived(0))
            playerPlaying.myScore.addScore(Board.commonGoalCards[0].checkPattern());
        if(!playerPlaying.isCommonGoalAchived(1))
            playerPlaying.myScore.addScore(Board.commonGoalCards[1].checkPattern());

        //checking if a player's shelf is full,
        // if true add +1pt and set the last lap
        if(playerPlaying.myShelfie.isShelfFull()) {
            playerPlaying.myScore.addScore(1);
            this.isOver = true;
        }
    }

    private void dealPersonalCards(){
        for(Player player : playersConnected){
            player.setCard(personalDeck.drawPersonal());
        }
    }

    private void setChair(){
        int index = (int) (Math.random()%playersConnected.size());
        playersConnected.get(index).setChair();
    }
    private void manageTurn(){
        setChair();
        dealPersonalCards();
        while(!isOver){
            for (Player player : playersConnected) {
                //a player can plays his turn if the match is not over
                // or if that is the last lap
                if(!isOver || !player.hasChair())
                    turn(player);
            }
        }
        for (Player player : playersConnected) {
            int spotScore = player.myShelfie.spotCheck();
            player.myScore.addScore(spotScore);
        }
        endGame();
    }

    private void endGame(){
        System.out.println("GAME OVER");
        Player temp;
        for (int i=0; i< playersConnected.size(); i++)
            for (int j=0; j< playersConnected.size(); j++){
                if(playersConnected.get(j).myScore.getScore() < playersConnected.get(j+1).myScore.getScore()){
                    temp = playersConnected.get(j);
                    playersConnected.set(j,playersConnected.get(j+1));
                    playersConnected.set(j+1, temp);
                }
            }
        for(int i=0; i< playersConnected.size(); i++){
            System.out.print("Position "+(i+1));
            System.out.println(playersConnected.get(i));
        }
    }
}
