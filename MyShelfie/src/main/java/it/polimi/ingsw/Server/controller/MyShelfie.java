package it.polimi.ingsw.Server.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.*;
import it.polimi.ingsw.utils.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.utils.Exceptions.NotEnoughSpaceInChosenColumnException;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import it.polimi.ingsw.Server.chat.ChatManager;
import it.polimi.ingsw.Server.chat.ChatMessage;
import it.polimi.ingsw.Server.chat.ChatStorage;
import it.polimi.ingsw.utils.PersistenceManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * MyShelfie class: this class represents the controller in the MVC design pattern. It offers methods to properly init all
 * data structures of the model, to manage players' turn, to manage critic network decisions linked to the status of the model
 * due to additional features of persistence, resilience and chat.
 */
public class MyShelfie {
    
    private final ArrayList<Player> playersConnected;
    private boolean isOver;
    private final PersonalGoalDeck personalDeck;
    private final CommonGoalDeck commonDeck;
    private final Board board;
    private int numberOfPlayers;
    private boolean isStarted;
    private ArrayList<ClientHandler> clientHandlers;
    private int currentPlayerIndex;
    private boolean allPlayersReady;
    private boolean firstTurn;
    private boolean gameOver;
    private final Object lock;
    private int firstToFinish;
    private final ChatManager chatManager;
    private boolean playerInCountdown;
    private final PersistenceManager persistenceManager;
    private final int gameIndex;
    private final boolean persistenceManaged;

    //- - - - - - - - - - - - - - - - - - CONSTRUCTORS - - - - - - - - -  - - - - - - - - - - - -
    /**
     * Constructor for new games
     * @param persistenceManager for the FA persistence
     * @param gameIndex the index of this game in game record
     * @author Irene Lo Presti
     */
    public MyShelfie(PersistenceManager persistenceManager, int gameIndex){
        this.board = new Board();
        this.commonDeck = new CommonGoalDeck();
        this.personalDeck = new PersonalGoalDeck();

        this.playersConnected = new ArrayList<>();
        this.numberOfPlayers = -1;
        this.clientHandlers = new ArrayList<>();
        this.lock = new Object();
        this.chatManager = new ChatManager();
        this.firstToFinish = -1;

        this.persistenceManager = persistenceManager;
        this.gameIndex = gameIndex;
        this.persistenceManaged = false;
    }

    /**
     * Constructor for old games to be restored after the server crashes
     * @param persistenceManager for the FA persistence
     * @param gameIndex the index of this game in game record
     * @param board: old board
     * @param commonGoalCardsNames: codes of the old common goal cards
     * @param currentPlayerIndex: index of the player that was playing the last turn
     * @param isStarted: boolean to know if the game was started
     * @param isOver: boolean to know if the games was finishing
     * @param numberOfPlayers: number of players connected to the old game
     * @param firstTurn: boolean to know if it is the first turn
     * @param firstToFinish: index of the first player who fill the board (-1 if nobody filled it)
     * @author Irene Lo Presti
     */
    public MyShelfie(PersistenceManager persistenceManager, int gameIndex, Board board,
                     String[] commonGoalCardsNames, int currentPlayerIndex, boolean isStarted, boolean isOver,
                     int numberOfPlayers, boolean firstTurn, int firstToFinish){
        this.commonDeck = new CommonGoalDeck();
        this.board = board;

        CommonGoalCard[] commonGoalCards = new CommonGoalCard[2];
        //get the right cards form the deck
        commonGoalCards[0] = commonDeck.getCard(commonGoalCardsNames[0]);
        commonGoalCards[1] = commonDeck.getCard(commonGoalCardsNames[1]);
        board.setCommonGoalCards(commonGoalCards);

        this.isOver = isOver;
        this.isStarted = isStarted;
        this.numberOfPlayers = numberOfPlayers;
        this.firstTurn = firstTurn;
        this.firstToFinish = firstToFinish;
        this.currentPlayerIndex = currentPlayerIndex;
        this.persistenceManager = persistenceManager;
        this.gameIndex = gameIndex;

        //initialize all the others attributes
        this.lock = new Object();
        this.allPlayersReady = true;
        this.personalDeck = new PersonalGoalDeck();
        this.playersConnected = new ArrayList<>();
        this.clientHandlers = new ArrayList<>();
        this.chatManager = new ChatManager();

        this.persistenceManaged = true; //this constructor is called after a server reconnection
    }

    //- - - - - - - - - - - - - - - - - - - -| L O G I N   M E T H O D S |- - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Checks if one player is already connected
     * @return true if the new player is the first one
     * @author Andrea Giacalone
     */
    public boolean isFirstConnected(){
        return (this.playersConnected.size() == 0);
    }

    /**
     * This method creates a player and add him to players list.
     * Starts the game if lobby has enough players
     * @param playerNickname: player's nickname
     * @param clientHandler: instance of client handler
     * @author Irene Lo Presti, Matteo Lussana
     */
    public void addPlayer(String playerNickname, ClientHandler clientHandler) {

        if (playersConnected.size() <= numberOfPlayers || numberOfPlayers==-1) {
            Player newPlayer = new Player(playerNickname);
            playersConnected.add(newPlayer);
            clientHandlers.add(clientHandler);
            chatManager.addChatter(playerNickname);

            if (playersConnected.size() == numberOfPlayers && !this.allPlayersReady)
                this.allPlayersReady = true;
        }
    }

    /**
     * This method sets the number of players of this game. If the players needed are already connected it
     * updates the boolean allPlayersReady in order to start the game. If too many players are connected it
     * sends them to another game
     * @param numberOfPlayers: number of players to be set
     * @author Irene Lo Presti
     */
    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        if((playersConnected.size()==numberOfPlayers) && (!this.allPlayersReady)){
            this.allPlayersReady = true;
        }
        else if(playersConnected.size() > numberOfPlayers){
            for(int i=playersConnected.size()-1; i>numberOfPlayers-1; i--) {
                persistenceManager.deletePlayerFile(playersConnected.get(i).getNickname());
                NumberOfPlayerManagementMsg msg = new NumberOfPlayerManagementMsg(playersConnected.get(i).getNickname());
                if(clientHandlers.get(i).isConnected())
                    clientHandlers.get(i).sendMessageToClient(msg);
                playersConnected.remove(i);
                clientHandlers.remove(i);
            }
            this.allPlayersReady = true;
        }
    }

    /**
     * OVERVIEW: this method allows to return to each player connected an updated snapshot of the current players waiting
     * in the lobby to start the game.
     * @author Andrea Giacalone
     */
    public void updateLobby(){
        ArrayList<String> lobbyPlayers = playersConnected.stream().map(Player::getNickname).collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i<playersConnected.size();i++) {
            LobbyUpdateAnswer msg = new LobbyUpdateAnswer(lobbyPlayers, allPlayersReady);
            if(clientHandlers.get(i).isConnected() && !(i == 0 && numberOfPlayers == -1))
                clientHandlers.get(i).sendMessageToClient(msg);
        }
    }

    //- - - - - - - - - - - - - - - - - - - -| GAME METHODS |- - - - - - - - - - - - - - - - - - - - - - -

        //- - - - - - - - - - -| INIT METHODS |- - - - - - - - - - - - - - - - - - - - - - - - -

        /**
         * This method is called when all players are connected and ready. It prepares the game (creation and
         * refill of the board, set of the chair, deal of personal cards, draw of common cards) and starts it.
         *
         * @author Irene Lo Presti
         */
        public void initGame(){
            if(!this.isStarted){
                this.isStarted = true;
                board.initGridParabolic(numberOfPlayers);
                //board.initGrid(numberOfPlayers);              : alternative option for initialize the grid
                board.refill();
                setChair();
                dealPersonalCards();
                drawCommonGoalCards();
                currentPlayerIndex = 0;
                firstTurn = true;
                updatePersistenceFile();
                startTurn();
            }
            else
                for(int i=0; i<playersConnected.size(); i++){
                    if(i!=currentPlayerIndex && clientHandlers.get(i).isConnected() && clientHandlers.get(i).getIsGui()){
                        GoWaitingGUI goWaitingGUI = new GoWaitingGUI();
                        clientHandlers.get(i).sendMessageToClient(goWaitingGUI);
                    }
            }
        }

        /**
         * OVERVIEW: this method calls the method drawPersonal for each player, so every player has his/her own
         * personal goal card, then it updates the player's file
         * @see Player
         * @see PersonalGoalDeck
         * @author Irene Lo Presti, Matteo Lussana
         */
        private void dealPersonalCards(){
            for(Player player : playersConnected) {
                PersonalGoalCard card = personalDeck.drawPersonal();
                player.setCard(card);
                //update the file
                String info = card.getId() + "\n0\nshelf\n"+player.getHasFinished()+"\n";
                persistenceManager.writeStaticPlayerInfo(player.getNickname(), info);
            }
        }

        /**
         * OVERVIEW: this method gives, randomly, a chair to one player and then updates the players' files
         * @author Irene Lo Presti, Matteo Lussana
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
            //update file
            for(Player player : playersConnected){
                String info = player.hasChair()+"\n";
                persistenceManager.writeStaticPlayerInfo(player.getNickname(), info);
            }
        }

        /**
         * OVERVIEW: this method draws 2 common goal cards from the deck
         * @author Irene Lo Presti, Matteo Lussana
         */
        private void drawCommonGoalCards(){
            CommonGoalCard[] commonGoalCards = new CommonGoalCard[2];
            commonGoalCards[0] = CommonGoalDeck.drawCommon();
            commonGoalCards[1] = CommonGoalDeck.drawCommon();
            board.setCommonGoalCards(commonGoalCards);
        }

    /**
     * OVERVIEW: it finds max pickable tiles by the current player and creates a message to send to
     * ChooseTilesFromBoardView. It also checks if this is the last mache, if so it updates the last lobby.
     * @author Irene Lo Presti, Matteo Lussana
     */
    private void startTurn() {
        boolean isBoardRefilled = true;
        //checking if the board need to be refilled
        if (board.needRefill()) {
            isBoardRefilled = board.refill();
        }

        if(isBoardRefilled) {
            // find max pickable tiles by the player
            int maxTilesPickable = playersConnected.get(currentPlayerIndex).getShelf().maxTilesPickable();

            ArrayList<String> playersNames = new ArrayList<>();
            for (int i = 0; i < numberOfPlayers; i++) {
                playersNames.add(playersConnected.get(i).getNickname());
            }

            YourTurnMsg yourTurnMsg;

            Tile[][] boardSnapshot = new Tile[9][9];

            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    boardSnapshot[i][j] = board.getBoardGrid()[i][j];

            Tile[][] shelfSnapshot = new Tile[6][5];

            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 5; j++)
                    shelfSnapshot[i][j] = playersConnected.get(currentPlayerIndex).getShelf().getGrid()[i][j];

            for (int i = 0; i < playersConnected.size(); i++) {
                if (i != currentPlayerIndex && clientHandlers.get(i).isConnected() && clientHandlers.get(i).getIsGui()) {
                    GoWaitingGUI goWaitingGUI = new GoWaitingGUI();
                    clientHandlers.get(i).sendMessageToClient(goWaitingGUI);
                }
            }

            yourTurnMsg = new YourTurnMsg(
                    playersConnected.get(currentPlayerIndex).getNickname(),
                    maxTilesPickable,
                    boardSnapshot, board.getCommonGoalCards(),
                    playersConnected.get(currentPlayerIndex).getPersonalGoalCard(),
                    firstTurn,
                    playersNames,
                    shelfSnapshot,
                    isOver
            );

            //if it's the last turn
            if (isOver)
                updateGameIsEndingView();


            //if the current player is connected, he/she goes to ChooseTilesFromBoardView
            if (clientHandlers.get(currentPlayerIndex).isConnected()) {
                clientHandlers.get(currentPlayerIndex).sendMessageToClient(yourTurnMsg);
            } else {
                computeCurrentPlayerIdx();
                startTurn();
            }
        }
        else{
            for(int i=0; i<numberOfPlayers; i++)
                endGame(i, true);
        }
    }

    /**
     * This method collects chosen tiles from the board
     * @param initialRow: user choice initial row
     * @param initialColumn: user choice initial column
     * @param direction: user choice direction
     * @param numberOfTiles: user choice tiles number
     */
    public void pickTilesFromBoard(int initialRow, int initialColumn, char direction, int numberOfTiles){
        ArrayList<Tile> chosenTiles = board.pickTilesFromBoard(initialRow, initialColumn, numberOfTiles, direction);
        playersConnected.get(currentPlayerIndex).setLittleHand(chosenTiles);
        redirectToPersonalShelf();
    }

    /**
     * This method sends the player the InsertInShelfView
     */
    private void redirectToPersonalShelf(){
        Player currentPlayer = playersConnected.get(currentPlayerIndex);

        Tile[][] matrix = new Tile[6][5];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = currentPlayer.getShelf().getGrid()[i][j];
            }
        }

        ArrayList<Tile> littleHand = new ArrayList<>(currentPlayer.getLittleHand());

        ToShelfMsg toShelfMsg = new ToShelfMsg(
                matrix,
                littleHand,
                board.getCommonGoalCards(),
                currentPlayer.getPersonalGoalCard()
        );
        if(clientHandlers.get(currentPlayerIndex).isConnected())
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(toShelfMsg);
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
        playersConnected.get(currentPlayerIndex).orderTiles(orderIdxs);
        playersConnected.get(currentPlayerIndex).getShelf().insert(columnIdx, playersConnected.get(currentPlayerIndex).getLittleHand());
        playersConnected.get(currentPlayerIndex).clearLittleHand();
    }

        //- - - - - - - - - - - - - - -| SCORE METHODS |- - - - - - - - - - - - - - - - - - - - - - - - -

        /**
         * This method computes the score of the current turn and manages the last lobby when the first
         * player fills the shelf
         * @author Matteo Lussana, Irene Lo Presti
         */
        public void computeTurnScore(){
            boolean isCommonGoalAchived = false;
            boolean isPersonalGoalAchived = false;
            boolean isShelfFull = false;

            //check goals and add score if necessary
            int personalPointsEarned = personalPointsEarned();
            if(personalPointsEarned != 0) {
                isPersonalGoalAchived = true;
                playersConnected.get(currentPlayerIndex).addScore(personalPointsEarned);
            }

            if(!playersConnected.get(currentPlayerIndex).isCommonGoalAchieved(0)) {
                int commonPointsEarned = commonPointsEarned(0);
                if(commonPointsEarned != 0) {
                    isCommonGoalAchived = true;
                    playersConnected.get(currentPlayerIndex).addScore(commonPointsEarned);
                }
            }
            if(!playersConnected.get(currentPlayerIndex).isCommonGoalAchieved(1)) {
                int commonPointsEarned = commonPointsEarned(1);
                if(commonPointsEarned != 0) {
                    isCommonGoalAchived = true;
                    playersConnected.get(currentPlayerIndex).addScore(commonPointsEarned);
                }
            }

            //checking if a player's shelf is full,
            // if true add +1pt and set the last lap
            if(playersConnected.get(currentPlayerIndex).getShelf().isShelfFull() && !isOver) {
                isShelfFull = true;
                playersConnected.get(currentPlayerIndex).addScore(1);
                this.isOver = true;

                this.firstToFinish = currentPlayerIndex;
                //if the first player to fill the shelf hasn't got the chair, the ones before he/she have
                //already played their last turn!
                for(int i=0; i<=currentPlayerIndex; i++) {
                    playersConnected.get(i).setHasFinished(true);
                }
            }
            if(isOver){
                playersConnected.get(currentPlayerIndex).setHasFinished(true);
            }

            updatePlayerFile();

            GoalAndScoreMsg goalAndScoreMsg = new GoalAndScoreMsg(isCommonGoalAchived, isPersonalGoalAchived, playersConnected.get(currentPlayerIndex).getScore(), isShelfFull, isOver);
            if(clientHandlers.get(currentPlayerIndex).isConnected())
                clientHandlers.get(currentPlayerIndex).sendMessageToClient(goalAndScoreMsg);
        }

        /**
         * OVERVIEW: this method checks if the player playing has achieved the number commonGoalIndex common goal,
         * and it returns the right amount of points
         * @see Player
         * @see CommonGoalCard
         * @param commonGoalIndex : number of the common goal to check (0 or 1)
         * @return commonPointsEarned >= 0
         * @author Irene Lo Presti
         */
        private int commonPointsEarned(int commonGoalIndex){
            CommonGoalCard card = board.getCommonGoalCard(commonGoalIndex);
            Tile[][] playerShelfSnapshot = playersConnected.get(currentPlayerIndex).getShelf().getGrid();
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
         * @author Irene Lo Presti
         */
        private int personalPointsEarned(){
            PersonalGoalCard card = playersConnected.get(currentPlayerIndex).getPersonalGoalCard();
            Tile[][] playerShelfSnapshot = playersConnected.get(currentPlayerIndex).getShelf().getGrid();
            return card.getPersonalGoalScore(playerShelfSnapshot);
        }

    //- - - - - - - - - - - - - - - - -| TURN MANAGEMENT METHODS |- - - - - - - - - - - - - - - - - - -

    /**
     * This method computes the current player index
     * @author Andrea Giacalone, Irene Lo Presti
     */
    private void computeCurrentPlayerIdx(){
            if(currentPlayerIndex == numberOfPlayers-1)
                currentPlayerIndex = 0;
            else
                currentPlayerIndex ++;
    }

    /**
     * This method sets the player who plays the next turn. It checks if the player is connected and if
     * he/she is the only one.
     * @author Irene Lo Presti, Andrea Giacalone
     */
    public void setNextPlayer(){
        //setting the next player as the current player
        computeCurrentPlayerIdx();

        updatePersistenceFile();

        //skipping when a player is disconnected from the game (FA Resilienza alle disconessioni)
        int numOfPlayersConnected = numberOfPlayers;

        while(numOfPlayersConnected>0){
            if(!clientHandlers.get(currentPlayerIndex).isConnected()){
                computeCurrentPlayerIdx();
                numOfPlayersConnected--;
            }
            else break;
        }

        if(numOfPlayersConnected == 1)
            startCountdown(currentPlayerIndex);
        else
            checkIfStartTurnOrEndTheGame();
    }

    /**
     * This method checks if the player can begin their turn or the game is over.
     * @author Irene Lo Presti, Andrea Giacalone
     */
    private void checkIfStartTurnOrEndTheGame(){

        //if a player is here the countdown has been interrupted
        if(playerInCountdown)
            playerInCountdown = false;

        if((!isOver || !playersConnected.get(currentPlayerIndex).hasChair())){
            if(firstTurn && currentPlayerIndex==0) {
                firstTurn = false;
                updatePersistenceFile();
            }
            goToCorrectView();
        }

        //entered when everyone played last turn
        else if(checkGameOver()){
            //spot check
            for (Player player : playersConnected) {
                int spotScore = player.getShelf().spotCheck();
                player.addScore(spotScore);
            }

            //sett last player has finished
            if(currentPlayerIndex==0) {
                playersConnected.get(numberOfPlayers - 1).setHasFinished(true);
            }else {
                playersConnected.get(currentPlayerIndex - 1).setHasFinished(true);
            }

            //set game over
            this.gameOver = true;

            updateGameIsEndingView();
        }

    }

    /**
     * This method checks if the game is over, two cases:
     *  - if all players are connected and everybody has played their turn then isOver == true and the
     *      current player is the one with the chair => the game is over
     *  - if not all players are connected, in particularly the one with the chair is disconnected, it's
     *      necessary to check if all the remaining players have played their last turn
     * @return true if the game is over, false otherwise
     * @author Irene Lo Presti
     */
    private boolean checkGameOver(){
        if(isOver && playersConnected.get(currentPlayerIndex).hasChair())
            return true;
        int  countPlayers = 0;
        for(int i=0; i<numberOfPlayers; i++){
            if((clientHandlers.get(i).isConnected() && playersConnected.get(i).getHasFinished()) ||
                    !clientHandlers.get(i).isConnected())
                countPlayers++;
        }
        return countPlayers == numberOfPlayers;
    }

    /**
     * FA: resilience
     * This method checks if the player has put the tiles in the shelf
     * @author Irene Lo Presti
     */
    private void goToCorrectView(){
        if(playersConnected.get(currentPlayerIndex).getLittleHand().size() == 0)
            startTurn();
        else
            redirectToPersonalShelf();
    }

    /**
     * This method sends the only player connected in countdown mode
     * @param playerIndex: index of the last player connected
     * @author Andrea Giacalone, Irene Lo Presti
     */
    private void startCountdown(int playerIndex){
        this.playerInCountdown = true;
        LastOneConnectedMsg msg = new LastOneConnectedMsg(playersConnected.get(playerIndex).getNickname());
        if(clientHandlers.get(playerIndex).isConnected())
            clientHandlers.get(playerIndex).sendMessageToClient(msg);
    }

    /**
     * This method checks if the player disconnected was in the middle of their turn. If so it sets
     * the next player. It also checks if the player who is disconnected is the first one and didn't
     * set the number of players, if so it disconnects all the other player from this game.
     * @param clientHandler of the player who is disconnected
     * @author Irene Lo Presti, Andrea Giacalone
     */
    public void shouldFinishTurn(ClientHandler clientHandler){
        //if the client disconnected was the actual one playing
        if(isStarted){
            if (clientHandlers.get(currentPlayerIndex).equals(clientHandler) && clientHandlers.stream().filter(ClientHandler::isConnected).toList().size()>=1) {
                setNextPlayer();
            }
        }
        else if(numberOfPlayers == -1 && clientHandlers.indexOf(clientHandler) == 0){
            clientHandler.getGameRecord().deleteGame(gameIndex);
            persistenceManager.deletePlayerFile(playersConnected.get(0).getNickname());
            if(playersConnected.size() > 1){
                for(int i=1; i<playersConnected.size(); i++){
                    persistenceManager.deletePlayerFile(playersConnected.get(i).getNickname());
                    NumberOfPlayerManagementMsg msg = new NumberOfPlayerManagementMsg(playersConnected.get(i).getNickname());
                    if(clientHandlers.get(i).isConnected())
                        clientHandlers.get(1).sendMessageToClient(msg);
                }
            }

        }
    }

    //- - - - - - - - - - - - - - - -| END GAME METHODS |- - - - - - - - - - - - - - - - - - - -

    /**
     * This method manages the update of the game is ending view every time a player plays their last turn.
     * @author Irene Lo Presti
     */
    public void updateGameIsEndingView(){
        String[] players = new String[numberOfPlayers];
        boolean[] hasFinished = new boolean[numberOfPlayers];
        for(int j=0; j<numberOfPlayers; j++){
            players[j] = playersConnected.get(j).getNickname();
            hasFinished[j] = playersConnected.get(j).getHasFinished();
        }

        for(int i=0; i<playersConnected.size(); i++) {
            //sending to gameIsEndingView players who have played their last turn
            if (playersConnected.get(i).getHasFinished() && clientHandlers.get(i).isConnected()) {
                clientHandlers.get(i).sendMessageToClient(
                        new GameIsEndingUpdate(gameOver, i, firstToFinish, players, hasFinished));
            }
        }
    }

    /**
     * This method sends the players to the scoreboard
     * @param playerIndex: index of the player going to the scoreboard
     * @param boardEmpty: true if the game ends because the tiles are finished
     * @author Matteo Lussana, Irene Lo Presti, Andrea Giacalone
     */
    public void endGame(int playerIndex, boolean boardEmpty) {
        synchronized (lock){
            ArrayList<String> playersNames = new ArrayList<>();
            for (int i = 0; i < numberOfPlayers; i++) {
                playersNames.add(playersConnected.get(i).getNickname());
            }

            ArrayList<Integer> scoreList = new ArrayList<>();
            for (Player player : playersConnected) {
                scoreList.add(player.getScore());
            }

            ScoreBoardMsg msg = new ScoreBoardMsg(playersNames, scoreList, playersNames.get(playerIndex), boardEmpty);
            if(clientHandlers.get(playerIndex).isConnected())
                clientHandlers.get(playerIndex).sendMessageToClient(msg);
        }
    }

    /**
     * This method disconnects the players from the game and delete their file
     * @param clientHandler of the player disconnecting
     * @author Andrea Giacalone, Irene Lo Presti
     */
    public void finishGame(ClientHandler clientHandler){
        if(!gameOver)
            gameOver = true;

        if(!clientHandler.getIsGui())
            clientHandler.stop();

        int playerIndex = clientHandlers.indexOf(clientHandler);
        fileDeleting(playersConnected.get(playerIndex).getNickname(), playerIndex);

    }

    /**
     * This method finds the correct client handler and calls finishGame
     * @param client: remote interface of the RMI client disconnecting
     * @author Andrea Giacalone, Irene Lo Presti
     */
    public void finishGameRMI(RemoteInterface client){
        ClientHandler clientHandler = null;
        for(int i=0; i<playersConnected.size(); i++){
            if(clientHandlers.get(i) != null && clientHandlers.get(i).getIsRMI() && clientHandlers.get(i).getClientInterface().equals(client)){
                clientHandler = clientHandlers.get(i);
                break;
            }
        }
        finishGame(clientHandler);
    }

    //- - - - - - - - - - - - - - - - -| CLIENT RESILIENCE |- - - - - - - - - - - - - - - - - - - - -

    /**
     * This method checks if the player with the parameter nickname is disconnected from this game
     * @param nickname of the player who wants to reconnect
     * @return the player index if the player is disconnected from this game, -1 otherwise
     * @author Irene Lo Presti, Andrea Giacalone
     */
    public int checkPlayerDisconnected(String nickname){
        int found = -1;
        for(int i = 0; i< playersConnected.size();i++){
            if(nickname.equals(playersConnected.get(i).getNickname()) && !clientHandlers.get(i).isConnected())
                found = i;
        }
        return found;
    }

    /**
     * This method switch the new client handler of the player reconnected with the one that he/she had before
     * the disconnection
     * @param playerIndex: index of the player reconnecting
     * @param clientHandlerReconnected: new client handler
     * @author Irene Lo Presti, Andrea Giacalone
     */
    public void switchClientHandler(int playerIndex, ClientHandler clientHandlerReconnected){
        clientHandlerReconnected.setGame(this);
        clientHandlers.set(playerIndex, clientHandlerReconnected);
    }

    /**
     * This method get the client handler of the player in countdown
     * @return the client handler of the player in countdown
     * @author Irene Lo Presti, Andrea Giacalone
     */
    public ClientHandler getCountdownClientHandler(){
        for(int i=0; i<numberOfPlayers; i++)
            if(clientHandlers.get(i).isConnected())
                return clientHandlers.get(i);
        return null;
    }

    //- - - - - - - - - - - - - - - - - - -| C H A T   M E T H O D S |- - - - - - - - - - - - - - - - - - - -

    /**
     * OVERVIEW: this method allows to return the record of chat messages filtered according to the their privacy flag.
     * The filtered chat will contain messages which are public, or, if private, only those whose receiver or whose sender
     * is equal to the requester of the chat record.
     * @param requester: the player who wants the chat to be shown.
     * @author Andrea Giacalone
     */
    public void getCustomChat(String requester){
        ChatStorage customChat = chatManager.getCustomChat(requester);
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getIsGui() && clientHandler.isConnected()) {
                clientHandler.sendMessageToClient(new ChatRecordAnswer(customChat));
            }
        }
            if(requester.equals(playersConnected.get(currentPlayerIndex).getNickname()) &&
                    clientHandlers.get(currentPlayerIndex).isConnected()) {
                clientHandlers.get(currentPlayerIndex).sendMessageToClient(new ChatRecordAnswer(customChat));
            }
    }

    /**
     * OVERVIEW: this method allow to update and so to add a valid message to the chat record of messages.
     * @param messageToSend: the message sent by the chatter.
     * @aythor Andrea Giacalone
     */
    public void updateChat(ChatMessage messageToSend){
        ChatMsgAnswer chatMsgAnswer;
        if(getChatManager().updateChat(messageToSend)){
            chatMsgAnswer = new ChatMsgAnswer(true);
        }else {
            chatMsgAnswer = new ChatMsgAnswer(false);
        }

        if (playersConnected.get(currentPlayerIndex).getNickname().equals(messageToSend.getSender()) &&
                clientHandlers.get(currentPlayerIndex).isConnected())
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(chatMsgAnswer);
    }

    //- - - - - - - - - - - - - - - - - - - -| P E R S I S T E N C E |- - - - - - - - - - - - - - - - - - - - -

    /**
     * This method is used after the server reconnection. It resets the old info of the players connected
     * to this game
     * @author Irene Lo Presti
     */
    public void resetPlayers(){
        int numberOfPlayersConnected = 0;
        for(Player player : playersConnected){
            String personalCardIndex = persistenceManager.setPlayer(player);
            if(personalCardIndex != null){
                PersonalGoalCard card = personalDeck.getCard(personalCardIndex);
                player.setCard(card);
                numberOfPlayersConnected++;
            }
        }

        if(numberOfPlayersConnected == 1) //also checking for 0?
            this.gameOver = true;
    }

    /**
     * This method updates the files for the FA persistence after all turns
     * @author Irene Lo Presti
     */
    public void updatePersistenceFile(){

        //set the string with all the info about this game
        StringBuilder update = new StringBuilder(Arrays.deepToString(board.getBoardGrid()) + "\n" +
                board.getCommonGoalCard(0).getCardInfo().getName() + "\n" +
                board.getCommonGoalCard(1).getCardInfo().getName() + "\n" +
                currentPlayerIndex + "\n" + isStarted + "\n" + numberOfPlayers + "\n" +
                board.getBag().toString() + "\n" + isOver + "\n" + firstTurn + "\n" + firstToFinish +"\n");

        for(int j=0; j<numberOfPlayers; j++)
            update.append(playersConnected.get(j).getNickname()).append("\n");
        //write the info on the game file
        persistenceManager.updateGameFile(gameIndex, update.toString());

    }

    /**
     * This method gets all the player's dynamic information and call the PersistenceManager to update
     * their file
     * @author Irene Lo Presti
     */
    private void updatePlayerFile(){
        Player player = playersConnected.get(currentPlayerIndex);
        //update of the player file
        String playerUpdate = player.getScore() + "\n" +
                Arrays.deepToString(player.getShelfGrid()) + "\n" +
                player.getHasFinished()+"\n";
        persistenceManager.updatePlayerFile(playersConnected.get(currentPlayerIndex).getNickname(), playerUpdate);
    }



    /**
     * This method manages the reconnection of the players after the server's connection drops
     * @param playerIndex: index of the player reconnecting
     * @author Irene Lo Presti
     */
    public void manageReconnectionPersistence(int playerIndex, boolean stopCountdown){
        if(playerIndex == currentPlayerIndex){
            if(firstTurn && playerIndex == 0)
                goToCorrectView();
            else checkIfStartTurnOrEndTheGame();
        }
        else if(clientHandlers.stream().filter(ClientHandler::isConnected).toList().size()==1)
            startCountdown(playerIndex);
        //check if the current player is connected, if not set another player to play
        else if(!clientHandlers.get(currentPlayerIndex).isConnected() || stopCountdown) setNextPlayer();
    }

    /**
     * This method deletes the player's file
     * @param playerNickname: nickname of the player
     * @author Irene Lo Presti
     */
    public void fileDeleting(String playerNickname, int playerIndex){
        //deleting player file
        persistenceManager.deletePlayerFile(playerNickname);
        numberOfPlayers--;
        if(numberOfPlayers==0) {
            clientHandlers.get(playerIndex).getGameRecord().deleteGame(playerIndex);
        }
    }

    //- - - - - - - - - - - - - - - - - -| GETTER METHODS |- - - - - - - - - - - - - - - - - - - - - - - - - -

    public boolean isGameOver(){
        return gameOver;
    }
    public boolean isPlayerInCountdown(){
        return playerInCountdown;
    }
    public Board getBoard(){
        return board;
    }
    public boolean getAllPlayersReady(){
        return allPlayersReady;
    }
    public boolean isStarted(){
        return this.isStarted;
    }
    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
    public ChatManager getChatManager() {
        return chatManager;
    }
    public boolean isPersistenceManaged(){
        return persistenceManaged;
    }

}
