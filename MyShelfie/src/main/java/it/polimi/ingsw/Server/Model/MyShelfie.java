package it.polimi.ingsw.Server.Model;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.Server.Model.Exceptions.NotEnoughSpaceInChosenColumnException;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static it.polimi.ingsw.Client.View.ColorCode.*;
import static it.polimi.ingsw.Client.View.ColorCode.RESET;

/**
 * CONTROLLER
 *
 * @author Matteo Lussana, Irene Lo Presti
 */
public class MyShelfie /*implements Runnable*/ {
    
    private final ArrayList<Player> playersConnected;
    private boolean isOver;
    private final PersonalGoalDeck personalDeck;
    private final CommonGoalDeck commonDeck;
    private final Board board;
    private int numberOfPlayers;
    private boolean isStarted;
    private final ArrayList<ClientHandler> clientHandlers;
    private int currentPlayerIndex;
    private static MyShelfie myShelfieInstance;

    private final Object lock;

    public MyShelfie(){
        this.board = Board.getBoardInstance();
        this.commonDeck = new CommonGoalDeck();
        this.personalDeck = new PersonalGoalDeck();

        this.isOver = false;
        this.isStarted = false;
        this.playersConnected = new ArrayList<>();
        this.numberOfPlayers = -1;
        this.clientHandlers = new ArrayList<>();

        this.lock = new Object();
    }

    /**
     * Method used for singleton design pattern
     * @return myShelfieInstance: the one that already exists or a new one
     */
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


    /**
     * Method that checks if player's nickname already exists
     * @param insertedString: new player's name
     * @return true if nickname is valid, false otherwise
     */
    public boolean checkNickname(String insertedString) {
        return(!(new ArrayList<String>(this.playersConnected.stream().map(x->x.getNickname()).collect(Collectors.toList()))).contains(insertedString));
    }

    /**
     * Checks if one player is already connected
     * @param insertedString: new player's nickname
     * @return true if the new player is the first one
     */
    public boolean isFirstConnected(String insertedString){
        return (this.playersConnected.size() == 0);
    }

    /**
     * This method creates a player and add him to players list.
     * Starts the game if lobby has enough players
     * @param playerNickname: player's nickname
     * @param clientHandler: instance of client handler
     */
    public void addPlayer(String playerNickname, ClientHandler clientHandler) {

        if (playersConnected.size() <= numberOfPlayers || numberOfPlayers==-1) {
            Player newPlayer = new Player(playerNickname);

            //aggiungo all'arraylist
            System.out.println("adding player...");
            playersConnected.add(newPlayer);
            clientHandlers.add(clientHandler);

            if (playersConnected.size() == numberOfPlayers) {
                this.isStarted = true;
                manageTurn();
            }
        }
    }

    public boolean isStarted(){
        return this.isStarted;
    }


    public void setNumberOfPlayers(int numberOfPlayers) {
            this.numberOfPlayers = numberOfPlayers;
    }

    public void manageLogin(ClientHandler clientHandler,LoginNicknameRequest loginNicknameRequest){
        S2CMessage loginNicknameAnswer;

        if (isStarted()==true) {
            loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.FULL_LOBBY);
            clientHandler.sendMessageToClient(loginNicknameAnswer);
            return;
        }


        if (checkNickname(loginNicknameRequest.getInsertedNickname()) == true){

            if(isFirstConnected(loginNicknameRequest.getInsertedNickname()) == true ){
                loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.FIRST_ACCEPTED);
                clientHandler.sendMessageToClient(loginNicknameAnswer);
                addPlayer(loginNicknameRequest.getInsertedNickname(),clientHandler);


            }else{
                loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.ACCEPTED);
                clientHandler.sendMessageToClient(loginNicknameAnswer);
                addPlayer(loginNicknameRequest.getInsertedNickname(),clientHandler);
            }


        }else {
            loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.INVALID);
            clientHandler.sendMessageToClient(loginNicknameAnswer);
        }

    }

    public void updateLobby(){
        ArrayList<String> lobbyPlayers = new ArrayList<>(playersConnected.stream().map(x->x.getNickname()).collect(Collectors.toList()));
        LobbyUpdateAnswer lobbyUpdateAnswer = new LobbyUpdateAnswer(lobbyPlayers);

        for(int i=0; i< clientHandlers.size(); i++){
            clientHandlers.get(i).sendMessageToClient(lobbyUpdateAnswer);
        }
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
        //System.out.println("sono in manage turn");
        board.initGridParabolic(numberOfPlayers);
        //board.initGrid(numberOfPlayers);
        board.refill();
        setChair();
        dealPersonalCards();
        drawCommonGoalCards();
        while(!isOver){
            for (int i = 0; i<numberOfPlayers; i++) {
                //a player can play his/her turn if the match is not over
                // or if that is the last lap
                if(!isOver || !playersConnected.get(i).hasChair()){
                    //saving the index of the player playing
                    currentPlayerIndex = i;
                    synchronized(lock){
                        turn(i);
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        //adding spot points
        for (Player player : playersConnected) {
            int spotScore = player.myShelfie.spotCheck();
            player.myScore.addScore(spotScore);
        }

        ArrayList<Integer> scoreList = new ArrayList<>();
        for (Player player : playersConnected) {
            scoreList.add(playersConnected.get(currentPlayerIndex).myScore.getScore());
        }
        ScoreBoardMsg scoreBoardMsg = new ScoreBoardMsg(playersConnected, scoreList);

        for (Player player : playersConnected) {
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(scoreBoardMsg);
        }
    }

    /**
     * OVERVIEW: it finds max pickable tiles by the current player and creates a message to send to
     * ChooseTilesFromBoardView
     */
    private void turn(int turnNumber) {

        // find max pickable tiles by the player
        int maxTilesPickable = playersConnected.get(currentPlayerIndex).myShelfie.maxTilesPickable();

        ArrayList<String> playersNames = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            playersNames.add(playersConnected.get(i).getNickname());
        }
        YourTurnMsg yourTurnMsg;
        yourTurnMsg = new YourTurnMsg(
                playersConnected.get(currentPlayerIndex).getNickname(),
                maxTilesPickable,
                board, Board.getCommonGoalCards(),
                playersConnected.get(currentPlayerIndex).getPersonalGoalCard(),
                turnNumber,
                playersNames
        );
        clientHandlers.get(currentPlayerIndex).sendMessageToClient(yourTurnMsg);
    }

    //funzione chiamata dal process message del messaggio creato alla fine dell'inserimento delle
    //tessere nella shelf del giocatore
    public void endOfTheTurn(){

        boolean isCommonGoalAchived = false;
        boolean isPersonalGoalAchived = false;
        boolean isShelfFull = false;

        //checking goals and adding score if necessary
        int personalPointsEarned = personalPointsEarned();
        if(personalPointsEarned != 0) {
            isPersonalGoalAchived = true;
            playersConnected.get(currentPlayerIndex).myScore.addScore(personalPointsEarned);
        }

        if(!playersConnected.get(currentPlayerIndex).isCommonGoalAchieved(0)) {
            int commonPointsEarned = commonPointsEarned(0);
            if(commonPointsEarned != 0) {
                isCommonGoalAchived = true;
                playersConnected.get(currentPlayerIndex).myScore.addScore(commonPointsEarned);
            }
        }
        if(!playersConnected.get(currentPlayerIndex).isCommonGoalAchieved(1)) {
            int commonPointsEarned = commonPointsEarned(1);
            if(commonPointsEarned != 0) {
                isCommonGoalAchived = true;
                playersConnected.get(currentPlayerIndex).myScore.addScore(commonPointsEarned);
            }
        }



        //checking if a player's shelf is full,
        // if true add +1pt and set the last lap
        if(playersConnected.get(currentPlayerIndex).myShelfie.isShelfFull() && !isOver) {
            isShelfFull = true;
            playersConnected.get(currentPlayerIndex).myScore.addScore(1);
            this.isOver = true;
        }
        //checking if the board need to be refilled
        if(board.needRefill())
            board.refill();

        GoalAndScoreMsg goalAndScoreMsg = new GoalAndScoreMsg(isCommonGoalAchived, isPersonalGoalAchived, playersConnected.get(currentPlayerIndex).myScore.getScore(), isShelfFull);
        clientHandlers.get(currentPlayerIndex).sendMessageToClient(goalAndScoreMsg);
    }

    /**
     * This method collect chosen tiles from the board and creates a
     * message which is sent to client in order to show InsertInShelfView
     * @param initialRow: user choice initial row
     * @param initialColumn: user choice initial column
     * @param direction: user choice direction
     * @param numberOfTiles: user choice tiles number
     */
    public void getPlayerChoice(int initialRow, int initialColumn, char direction, int numberOfTiles){

        Player currentPlayer = playersConnected.get(currentPlayerIndex);

        board.pickTilesFromBoard(initialRow, initialColumn, numberOfTiles, direction, currentPlayer);

        MyShelfMsg myShelfMsg = new MyShelfMsg(
                currentPlayer.myShelfie.getGrid(),
                currentPlayer.getLittleHand(),
                Board.getCommonGoalCards(),
                currentPlayer.getPersonalGoalCard()
                );
        clientHandlers.get(currentPlayerIndex).sendMessageToClient(myShelfMsg);
    }

    /**
     * This method calls other methods to reorder the tiles that the player hold
     * and insert them in the shelf's chosen column
     * @param columnIdx: chosen column index
     * @param orderIdxs: array of indexes to order tiles
     * @throws InvalidTileIndexInLittleHandException: thrown to avoid impossible order indexes
     * @throws NotEnoughSpaceInChosenColumnException: thrown if chosen column is full of tiles
     */
    public void insertingTiles(int columnIdx,int[] orderIdxs) throws InvalidTileIndexInLittleHandException, NotEnoughSpaceInChosenColumnException {

        Player currentPlayer = playersConnected.get(currentPlayerIndex);

        /*
        System.out.println(currentPlayer.getLittleHand());
        for (int orderIdx : orderIdxs) {
            System.out.print(orderIdx + " ");
        }
        System.out.println();
        */

        currentPlayer.getTiles(orderIdxs);
        currentPlayer.orderTiles(currentPlayer.getLittleHand(),orderIdxs);
        currentPlayer.myShelfie.insert(columnIdx,playersConnected.get(currentPlayerIndex).getLittleHand());

        /*
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(currentPlayer.myShelfie.getGrid()[i][j] + "\t");
            }
            System.out.println();
        }
        */
    }

    /**
     * OVERVIEW: this method checks if the player playing has achieved the number commonGoalIndex common goal,
     * and it returns the right amount of points
     * @see Player
     * @see CommonGoalCard
     * @param commonGoalIndex : number of the common goal to check (0 or 1)
     * @return commonPointsEarned >= 0
     */
    private int commonPointsEarned(int commonGoalIndex){
        CommonGoalCard card = Board.getCommonGoalCard(commonGoalIndex);
        Tile[][] playerShelfSnapshot = playersConnected.get(currentPlayerIndex).myShelfie.getGrid();
        if(card.checkPattern(playerShelfSnapshot)) {
            playersConnected.get(currentPlayerIndex).setCommonGoalAchieved(commonGoalIndex);
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
     * @return personalPointsEarned >= 0
     */
    private int personalPointsEarned(){
        PersonalGoalCard card = playersConnected.get(currentPlayerIndex).getPersonalGoalCard();
        Tile[][] playerShelfSnapshot = playersConnected.get(currentPlayerIndex).myShelfie.getGrid();
        return card.getPersonalGoalScore(playerShelfSnapshot);
    }

    public void finishTurn(){
        synchronized (lock){
            System.out.println("finito, chiamo l'altro");
            lock.notify();
        }
    }

}
