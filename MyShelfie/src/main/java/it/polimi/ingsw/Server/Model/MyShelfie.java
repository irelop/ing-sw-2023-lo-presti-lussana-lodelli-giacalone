package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.ExceptionMsg;
import it.polimi.ingsw.Server.Messages.YourTurnMsg;
import it.polimi.ingsw.Server.Model.Exceptions.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
    private ArrayList<ClientHandler> clientHandlers;

    private static MyShelfie myShelfieInstance;

    public MyShelfie(){
        this.board = Board.getBoardInstance();
        this.commonDeck = new CommonGoalDeck();
        this.personalDeck = new PersonalGoalDeck();
        this.isOver = false;
        this.started = false;
        this.playersConnected = new ArrayList<>();
        this.numberOfPlayers = -1;
        this.clientHandlers = new ArrayList<ClientHandler>();
    }

    public static MyShelfie getMyShelfie(){
        //oppure abbiamo raggiunto max giocatori => nuova partita
        if(myShelfieInstance == null){
            myShelfieInstance = new MyShelfie();
        }
        return myShelfieInstance;
    }

    public Board getBoard(){
        return board;
    }

    //- - - - - - - - - - - - - - - - - - - -| L O G I N   M E T H O D S |- - - - - - - - - - - - - - - - - - - - - - - -

    public boolean checkNickname(String insertedString) {
        return(!(this.playersConnected.contains(insertedString)));
    }

    public boolean isFirstConnected(String insertedString){
        return (this.playersConnected.indexOf(insertedString) == 0);
    }


    //chiamata dal messaggio di login con il nickname del player

    /*
        edit ANDREA: nel processMessage() di LoginNicknameAnswer se il nickname è valido lo chiamo così da aggiungerlo.
     */
    public void addPlayer(String playerNickname, ClientHandler clientHandler){
        //creo player
        Player newPlayer = new Player(playerNickname);
        //aggiungo all'arraylist
        playersConnected.add(newPlayer);
        clientHandlers.add(clientHandler);


        if(playersConnected.size()==numberOfPlayers){
            this.started = true;
            manageTurn();
        }

    }

    //la gestiamo con un'eccezione o va bene così????
    //chiamata da login view SOLO con il primo giocatore connesso

    //edit ANDREA: ho gestito lato view i casi in cui l'input non sia valido con annessa stampa all'utente quindi direi ok.
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
        ClientHandler firstPlayerClientHandler = clientHandlers.get(index);
        firstPlayer.setChair();
        playersConnected.remove(firstPlayer);
        playersConnected.add(0, firstPlayer);
        clientHandlers.remove(firstPlayerClientHandler);
        clientHandlers.add(0, firstPlayerClientHandler);
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
            for (int i = 0; i<numberOfPlayers; i++) {
                //a player can play his/her turn if the match is not over
                // or if that is the last lap
                if(!isOver || !playersConnected.get(i).hasChair()){
                    //call the turn for the player, sending him and his clientHandler
                    turn(playersConnected.get(i), clientHandlers.get(i));
                }
            }
        }
        for (Player player : playersConnected) {
            int spotScore = player.myShelfie.spotCheck();
            player.myScore.addScore(spotScore);
        }
        //endGame();
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
    private void turn(Player playerPlaying, ClientHandler clientHandler){

        // find max pickable tiles by the player
        int maxTilesPickable = playerPlaying.myShelfie.maxTilesPickable();

        YourTurnMsg yourTurnMsg;
        yourTurnMsg = new YourTurnMsg(playerPlaying.getNickname(), maxTilesPickable, Board.getBoardGrid());
        clientHandler.sendMessageToClient(yourTurnMsg);


        chooseTilesFromBoard();

        // the player chooses the tiles from the board
        ArrayList<Tile> chosenTiles = board.chooseTilesFromBoard(maxTilesPickable);

        //copy of the tiles chosen in the player littleHand already in the correct order
        playerPlaying.orderTiles(chosenTiles, playerPlaying.askOrder(chosenTiles));

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



    public void chooseTilesFromBoard(){
        do{
            try{
                int initialPositionR = getInitialRow();
                int initialPositionC = getInitialColumn();
                checkPosition(initialPositionR, initialPositionC);
                break;
            }catch(OutOfBoardException | InvalidPositionException | InvalidCellException | EmptyCellException e){
                System.out.println(e);
            }
        }while(true);
    }
    public void checkPosition(int row, int column){
        try{
            row--; column--;
            board.checkPosition(row, column);
        }catch(OutOfBoardException | InvalidPositionException | InvalidCellException | EmptyCellException e){
            ExceptionMsg exceptionMsg = new ExceptionMsg(e.toString());
        }
    }

}
