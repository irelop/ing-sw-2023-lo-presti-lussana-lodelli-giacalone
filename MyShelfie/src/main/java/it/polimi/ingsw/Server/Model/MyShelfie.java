package it.polimi.ingsw.Server.Model;

import java.util.ArrayList;
import java.util.Random;

/**
 * CONTROLLER
 *
 * @author Matteo Lussana, Irene Lo Presti
 */
public class MyShelfie {
    private final ArrayList<Player> playersConnected;
    private boolean isOver;
    private final PersonalGoalDeck personalDeck;
    private final CommonGoalDeck commonDeck;
    private final Board board;
    private int numberOfPlayers;
    private boolean started;

    private static MyShelfie myShelfieInstance;

    public MyShelfie(){
        this.board = Board.getBoardInstance();
        this.commonDeck = new CommonGoalDeck();
        this.personalDeck = new PersonalGoalDeck();
        this.isOver = false;
        this.started = false;
        this.playersConnected = new ArrayList<>();
        this.numberOfPlayers = -1;
    }

    public static MyShelfie getMyShelfie(){
        //oppure abbiamo raggiunto max giocatori => nuova partita
        if(myShelfieInstance == null){
            myShelfieInstance = new MyShelfie();
        }
        return myShelfieInstance;
    }

    //chiamata dal messaggio di login con il nickname del player
    public void addPlayer(String playerNickname){
        //creo player
        Player newPlayer = new Player(playerNickname);
        //aggiungo all'arraylist
        playersConnected.add(newPlayer);

        if(playersConnected.size()==numberOfPlayers){
            this.started = true;
            manageTurn();
        }

    }

    //la gestiamo con un'eccezione o va bene così????
    //chiamata da login view SOLO con il primo giocatore connesso
    public void setNumberOfPlayers(int numberOfPlayers) {
        if(numberOfPlayers == -1)
            this.numberOfPlayers = numberOfPlayers;
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
        //lo diciamo al giocatore che è primo?
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
            for (Player playerPlaying : playersConnected) {
                //a player can play his/her turn if the match is not over
                // or if that is the last lap
                if(!isOver || !playerPlaying.hasChair()){
                    for(Player playerWaiting : playersConnected){
                        if(playerWaiting != playerPlaying)
                    }
                    turn(player);
                }
            }
        }
        for (Player player : playersConnected) {
            int spotScore = player.myShelfie.spotCheck();
            player.myScore.addScore(spotScore);
        }
        endGame();
    }

    private void turn(Player playerPlaying){

    }

}
