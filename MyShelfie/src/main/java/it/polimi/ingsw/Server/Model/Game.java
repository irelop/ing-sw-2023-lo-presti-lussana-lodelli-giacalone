package it.polimi.ingsw.Server.Model;

/**
 * Game class: this class manages the single game, composed by 2 to 4 players, a board,
 * a deck of common goal cards and a deck of personal goal card. This class manages the turn of every single
 * player and the end of the game.
 * @deprecated
 * @authors Matteo Lussana, Irene Lo Presti
 */

import it.polimi.ingsw.Server.Model.Exceptions.NotEnoughSpaceInChosenColumnException;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private final ArrayList<Player> playersConnected;
    private boolean isOver;
    private final PersonalGoalDeck personalDeck;
    private final CommonGoalDeck commonDeck;
    private final Board board;

    /**
     * OVERVIEW: Constructor: it initializes the ArrayList of players and creates the two decks and the board.
     * @see Player
     * @see Board
     * @see PersonalGoalDeck
     * @see CommonGoalDeck
     * @param playersConnected : all the players connected
     */
    public Game(ArrayList<Player> playersConnected){
        //fill the arrayList of players
        this.playersConnected = new ArrayList<Player>();
        this.playersConnected.addAll(playersConnected);

        this.isOver = false;

        this.personalDeck = new PersonalGoalDeck();
        this.commonDeck = new CommonGoalDeck();
        this.board = new Board();
    }

    public Board getBoard(){
        return this.board;
    }


    /**
     * OVERVIEW: it manages all the moves of a single player during his turn:
     *          1) find max pickable tiles by the player
     *          2) the player chooses the tiles from the board
     *          3) copy of the tiles chosen in the player littleHand already in the correct order
     *          4) check of the goals and adding score if necessary
     *          5) check if a player's shelf is full, if it is the method adds one point
     *          6) check if the board needs to be refilled, if it is true it calls the refill method
     * @see Player
     * @see Board
     * @param playerPlaying: players that are playing
     */
    private void turn(Player playerPlaying){

        // find max pickable tiles by the player
        int maxTilesPickable = playerPlaying.myShelfie.maxTilesPickable();

        // the player chooses the tiles from the board
        ArrayList<Tile> chosenTiles = board.chooseTilesFromBoard(maxTilesPickable);

        //copy of the tiles chosen in the player littleHand already in the correct order
        playerPlaying.orderTiles(chosenTiles, playerPlaying.getTiles(chosenTiles););

        do{
            try {
                System.out.println("choose the Column:");
                Scanner scanner = new Scanner(System.in);
                int columnChosen = scanner.nextInt();

                playerPlaying.myShelfie.insert(columnChosen, playerPlaying.getLittleHand());
                break;
            } catch (NotEnoughSpaceInChosenColumnException e){
                System.out.println(e);
            }
        }while(true);

        //checking goals and adding score if necessary
        playerPlaying.myScore.addScore(personalPointsEarned(playerPlaying));

        if(!playerPlaying.isCommonGoalAchieved(0))
            playerPlaying.myScore.addScore(commonPointsEarned(playerPlaying, 0));
        if(!playerPlaying.isCommonGoalAchieved(1))
            playerPlaying.myScore.addScore(commonPointsEarned(playerPlaying, 1));

        //checking if a player's shelf is full,
        // if true add +1pt and set the last lap
        if(playerPlaying.myShelfie.isShelfFull()) {
            playerPlaying.myScore.addScore(1);
            this.isOver = true;
        }

        //checking if the board need to be refilled
        if(board.needRefill())
            board.refill();
    }

    /**
     * OVERVIEW: this method checks if the player playing has achieved the number commonGoalIndex common goal,
     * and it returns the right amount of points
     * @see Player
     * @see CommonGoalCard
     * @param playerPlaying : Player who is playing his/her turn
     * @param commonGoalIndex : number of the common goal to check (0 or 1)
     * @return commonPointsEarned >= 0
     */
    private int commonPointsEarned(Player playerPlaying, int commonGoalIndex){
        CommonGoalCard card = Board.getCommonGoalCard(commonGoalIndex);
        Tile[][] playerShelfSnapshot = playerPlaying.myShelfie.getGrid();
        if(card.checkPattern(playerShelfSnapshot)) {
            playerPlaying.setCommonGoalAchieved(commonGoalIndex);
            return card.getScore();
        }
        else return 0;
    }

    /**
     * OVERVIEW: this method checks if the player playing has achieved some personal goals, and it returns
     * the right amount of points
     * @see Player
     * @see PersonalGoalCard
     * @see Tile
     * @param playerPlaying : Player who is playing his/her turn
     * @return personalPointsEarned >= 0
     */
    private int personalPointsEarned(Player playerPlaying){
        PersonalGoalCard card = playerPlaying.getPersonalGoalCard();
        Tile[][] playerShelfSnapshot = playerPlaying.myShelfie.getGrid();
        return card.getPersonalGoalScore(playerShelfSnapshot);
    }

    /**
     * OVERVIEW: this method calls the method drawPersonal for each player, so every player has his / her own
     * personal goal card
     * @see Player
     * @see PersonalGoalDeck
     */
    private void dealPersonalCards(){
        for(Player player : playersConnected){
            player.setCard(personalDeck.drawPersonal());
        }
    }

    /**
     * OVERVIEW: this method gives, randomly, a chair to one player
     */
    private void setChair(){
        Random random = new Random();
        int index = random.nextInt(playersConnected.size());
        Player firstPlayer = playersConnected.get(index);
        firstPlayer.setChair();
        playersConnected.remove(firstPlayer);
        playersConnected.add(0, firstPlayer);
    }


    /**
     * OVERVIEW: this method draws 2 common goal cards from the deck
     */
    private void drawCommonGoalCards(){
        CommonGoalCard[] commonGoalCards = new CommonGoalCard[2];
        commonGoalCards[0] = CommonGoalDeck.drawCommon();
        commonGoalCards[1] = CommonGoalDeck.drawCommon();
        board.setCommonGoalCards(commonGoalCards);
    }

    /**
     * OVERVIEW: this method manages the game: a player can play his/her turn if the match is not over
     *       or if that is the last lap
     */
    public void manageTurn(){
        setChair();
        dealPersonalCards();
        drawCommonGoalCards();
        while(!isOver){
            for (Player player : playersConnected) {
                //a player can play his/her turn if the match is not over
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

    /**
     * OVERVIEW: this method prints the leaderboard of the game (nickname and score for each player)
     */
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
