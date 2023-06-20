package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.Server.Model.Exceptions.NotEnoughSpaceInChosenColumnException;
import it.polimi.ingsw.Server.RMIClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * CONTROLLER
 */
public class MyShelfie {
    
    private final ArrayList<Player> playersConnected;
    private boolean isOver;
    private final PersonalGoalDeck personalDeck;
    private final CommonGoalDeck commonDeck;
    private final Board board;
    private int numberOfPlayers;
    private boolean isStarted;
    private final ArrayList<ClientHandler> clientHandlers;
    private int currentPlayerIndex;
    private boolean allPlayersReady;
    private boolean firstTurn;
    private boolean gameOver;
    private final Object lock;
    private int firstToFinish;
    private final ChatManager chatManager;
    private boolean playerInCountdown;

    private PersistenceManager persistenceManager;
    private int gameIndex;

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

        this.isOver = false;
        this.isStarted = false;
        this.playersConnected = new ArrayList<>();
        this.numberOfPlayers = -1;
        this.clientHandlers = new ArrayList<>();

        this.allPlayersReady = false;
        this.gameOver = false;
        this.lock = new Object();
        this.chatManager = new ChatManager();
        this.playerInCountdown = false;

        this.persistenceManager = persistenceManager;
        this.gameIndex = gameIndex;
    }



    /**
     * Constructor for old games to be restored after the server crash
     * @param persistenceManager for the FA persistence
     * @param gameIndex the index of this game in game record
     * @param board: old board
     * @param commonGoalCardsNames: codes of the old common goal cards
     * @param currentPlayerIndex: index of the player that was playing the last turn
     * @param isStarted: boolean to know if the game was started
     * @param isOver: boolean to know if the games was finishing
     * @param numberOfPlayers: number of players connected to the old game
     *
     * @author Irene Lo Presti
     */
    public MyShelfie(PersistenceManager persistenceManager, int gameIndex, Board board, String[] commonGoalCardsNames, int currentPlayerIndex, boolean isStarted, boolean isOver, int numberOfPlayers){
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

        //initialize all the others attributes
        this.gameOver = false;
        this.lock = new Object();
        this.allPlayersReady = true;
        this.personalDeck = new PersonalGoalDeck();
        this.playersConnected = new ArrayList<>();
        this.clientHandlers = new ArrayList<>();
        this.chatManager = new ChatManager();
        this.playerInCountdown = false;

        this.persistenceManager = persistenceManager;
        this.gameIndex = gameIndex;
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
                NumberOfPlayerManagementMsg msg = new NumberOfPlayerManagementMsg(playersConnected.get(i).getNickname());
                clientHandlers.get(i).sendMessageToClient(msg);
                playersConnected.remove(i);
                clientHandlers.remove(i);
            }
            this.allPlayersReady = true;
        }
    }

    public void updateLobby(){
        ArrayList<String> lobbyPlayers = playersConnected.stream().map(Player::getNickname).collect(Collectors.toCollection(ArrayList::new));

        for (int i = 0; i<playersConnected.size();i++) {
            LobbyUpdateAnswer msg = new LobbyUpdateAnswer(lobbyPlayers, allPlayersReady);
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
                updatePersistenceFiles();
                startTurn();
            }
        }

        /**
         * OVERVIEW: this method calls the method drawPersonal for each player, so every player has his / her own
         * personal goal card
         * @see Player
         * @see PersonalGoalDeck
         * @author Irene Lo Presti, Matteo Lussana
         */
        private void dealPersonalCards(){
            for(Player player : playersConnected) {
                PersonalGoalCard card = personalDeck.drawPersonal();
                player.setCard(card);
                String info = card.getId() + "\n0\nshelf\n";
                //writeOnPlayersFiles(info, player.getNickname());
                persistenceManager.writeStaticPlayerInfo(player.getNickname(), info);
            }
        }

        /**
         * OVERVIEW: this method gives, randomly, a chair to one player
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

            for(Player player : playersConnected){
                String info = player.hasChair()+"\n";
                //writeOnPlayersFiles(info, player.getNickname());
                persistenceManager.writeStaticPlayerInfo(player.getNickname(), info);
            }

        }

        /**
         * OVERVIEW: this method draws 2 common goal cards from the deck
         * @auhtor Irene Lo Presti, Matteo Lussana
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

        //checking if the board need to be refilled
        if (board.needRefill())
            board.refill();

        // find max pickable tiles by the player
        int maxTilesPickable = playersConnected.get(currentPlayerIndex).myShelfie.maxTilesPickable();

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
                shelfSnapshot[i][j] = playersConnected.get(currentPlayerIndex).myShelfie.getGrid()[i][j];

        for(int i=0; i<playersConnected.size(); i++){
            if(i!=currentPlayerIndex && clientHandlers.get(i).isConnected()){
                GoWaitingGUI goWaitingGUI = new GoWaitingGUI();
                clientHandlers.get(i).sendMessageToClient(goWaitingGUI);
            }


        }

        yourTurnMsg = new YourTurnMsg(
                playersConnected.get(currentPlayerIndex).getNickname(),
                maxTilesPickable,
                boardSnapshot, Board.getCommonGoalCards(),
                playersConnected.get(currentPlayerIndex).getPersonalGoalCard(),
                firstTurn,
                playersNames,
                shelfSnapshot,
                isOver
        );

        //if it's the last turn
        if (isOver) {

            //the previous player has played their last turn
            if (currentPlayerIndex == 0)
                playersConnected.get(numberOfPlayers - 1).setHasFinished(true);
            else
                playersConnected.get(currentPlayerIndex - 1).setHasFinished(true);


            updateGameIsEndingView();
        }


        //if the current player is connected, he/she goes to ChooseTilesFromBoardView
        if(clientHandlers.get(currentPlayerIndex).isConnected()){
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(yourTurnMsg);
        }
        else{
            computeCurrentPlayerIdx();
            startTurn();
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
        Player currentPlayer = playersConnected.get(currentPlayerIndex);
        board.pickTilesFromBoard(initialRow, initialColumn, numberOfTiles, direction, currentPlayer);
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
                matrix[i][j] = currentPlayer.myShelfie.getGrid()[i][j];
            }
        }

        ArrayList<Tile> littleHand = new ArrayList<>(currentPlayer.getLittleHand());

        ToShelfMsg toShelfMsg = new ToShelfMsg(
                matrix,
                littleHand,
                Board.getCommonGoalCards(),
                currentPlayer.getPersonalGoalCard()
        );

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

        Player currentPlayer = playersConnected.get(currentPlayerIndex);

        currentPlayer.getTiles(orderIdxs);
        currentPlayer.orderTiles(currentPlayer.getLittleHand(),orderIdxs);
        currentPlayer.myShelfie.insert(columnIdx,playersConnected.get(currentPlayerIndex).getLittleHand());
        currentPlayer.clearLittleHand();

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
            if(playersConnected.get(currentPlayerIndex).myShelfie.isShelfFull() &&  !isOver) {
                isShelfFull = true;
                playersConnected.get(currentPlayerIndex).myScore.addScore(1);
                this.isOver = true;

                this.firstToFinish = currentPlayerIndex;
                //if the first player to fill the shelf hasn't got the chair, the ones before he/she have
                //already played their last turn!
                for(int i=0; i<=currentPlayerIndex; i++) {
                    playersConnected.get(i).setHasFinished(true);
                }
            }

            updatePersistenceFiles();

            GoalAndScoreMsg goalAndScoreMsg = new GoalAndScoreMsg(isCommonGoalAchived, isPersonalGoalAchived, playersConnected.get(currentPlayerIndex).myScore.getScore(), isShelfFull, isOver);
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
         * @author Irene Lo Presti
         */
        private int personalPointsEarned(){
            PersonalGoalCard card = playersConnected.get(currentPlayerIndex).getPersonalGoalCard();
            Tile[][] playerShelfSnapshot = playersConnected.get(currentPlayerIndex).myShelfie.getGrid();
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

        if(!isOver || !playersConnected.get(currentPlayerIndex).hasChair()){
            if(firstTurn && currentPlayerIndex==0) {
                firstTurn = false;
            }

            //FA: resilience
                // check if the player has put the tiles in the shelf
            if(playersConnected.get(currentPlayerIndex).getLittleHand().size() == 0)
                startTurn();
            else
               redirectToPersonalShelf();
        }
        //entered when everyone played last turn
        if(isOver && playersConnected.get(currentPlayerIndex).hasChair()){
            //spot check
            for (Player player : playersConnected) {
                int spotScore = player.myShelfie.spotCheck();
                player.myScore.addScore(spotScore);
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
     * This method sends the only player connected in countdown mode
     * @param playerIndex: index of the last player connected
     * @author Andrea Giacalone, Irene Lo Presti
     */
    private void startCountdown(int playerIndex){
        this.playerInCountdown = true;
        LastOneConnectedMsg msg = new LastOneConnectedMsg(playersConnected.get(playerIndex).getNickname());
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
            clientHandler.getGameRecord().deleteGame(this, playersConnected.get(0).getNickname());
            if(playersConnected.size() > 1){
                for(int i=1; i<playersConnected.size(); i++){
                    NumberOfPlayerManagementMsg msg = new NumberOfPlayerManagementMsg(playersConnected.get(i).getNickname());
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

        for(int i=0; i<numberOfPlayers; i++) {
            //sending to gameIsEndingView players who have played their last turn
            if (playersConnected.get(i).getHasFinished() && clientHandlers.get(i).isConnected()) {
                clientHandlers.get(i).sendMessageToClient(
                        new GameIsEndingUpdateAnswer(gameOver, i, firstToFinish, players, hasFinished));
            }
        }

    }

    /**
     * This method sends the players to the scoreboard
     * @param playerIndex: index of the player going to the scoreboard
     * @author Matteo Lussana, Irene Lo Presti, Andrea Giacalone
     */
    public void endGame(int playerIndex) {

        synchronized (lock){
            ArrayList<String> playersNames = new ArrayList<>();
            for (int i = 0; i < numberOfPlayers; i++) {
                playersNames.add(playersConnected.get(i).getNickname());
            }

            ArrayList<Integer> scoreList = new ArrayList<>();
            for (Player player : playersConnected) {
                scoreList.add(player.myScore.getScore());
            }

            ScoreBoardMsg msg = new ScoreBoardMsg(playersNames, scoreList, playersNames.get(playerIndex));
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

        clientHandler.stop();

        int playerIndex = clientHandlers.indexOf(clientHandler);
        fileDeleting(playersConnected.get(playerIndex).getNickname());

    }

    /**
     * This method finds the correct client handler and calls finishGame
     * @param client: remote inteface of the RMI client disconnecting
     * @author Andrea Giacalone, Irene Lo Presti
     */
    public void finishGameRMI(RemoteInterface client){
        ClientHandler clientHandler = null;
        for(int i=0; i<playersConnected.size(); i++){
            if(clientHandlers.get(i) != null && clientHandlers.get(i).isRMI() && clientHandlers.get(i).getClientInterface().equals(client)){
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
        for(int i = 0; i< numberOfPlayers;i++){
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

    //- - - - - - - - - - - - - - - - - - -| CHAT |- - - - - - - - - - - - - - - - - - - -


    public void getCustomChat(String requester){
        ChatStorage customChat = chatManager.getCustomChat(requester);
        if (!clientHandlers.get(currentPlayerIndex).isRMI())
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(new ChatRecordAnswer(customChat));
        else {
            try {
                clientHandlers.get(currentPlayerIndex).getClientInterface().sendMessageToClient(new ChatRecordAnswer(customChat));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateChat(ChatMessage messageToSend){
        ChatMsgAnswer chatMsgAnswer;
        if(getChatManager().updateChat(messageToSend)==true){
            chatMsgAnswer = new ChatMsgAnswer(true);
        }else {
            chatMsgAnswer = new ChatMsgAnswer(false);
        }
        if(!clientHandlers.get(currentPlayerIndex).isRMI())
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(chatMsgAnswer);
        else {
            try {
                clientHandlers.get(currentPlayerIndex).getClientInterface().sendMessageToClient(chatMsgAnswer);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //- - - - - - - - - - - - - - - - - - - -| PERSISTENCE |- - - - - - - - - - - - - - - - - - - - -

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

        if(numberOfPlayersConnected == 1) //controllo anche se sono 0?
            this.gameOver = true;
    }

    /**
     * This method updates the files for the FA persistence after all turns
     * @author Irene Lo Presti
     */
    public void updatePersistenceFiles(){

        //set the string with all the info about this game
        StringBuilder update = new StringBuilder(Arrays.deepToString(board.getBoardGrid()) + "\n" +
                board.getCommonGoalCard(0).getCardInfo().getName() + "\n" +
                board.getCommonGoalCard(1).getCardInfo().getName() + "\n" +
                currentPlayerIndex + "\n" + isStarted + "\n" + numberOfPlayers + "\n" +
                board.getBag().toString() + "\n" + isOver + "\n");

        for(int j=0; j<numberOfPlayers; j++)
            update.append(playersConnected.get(j).getNickname()).append("\n");

        //write the info on the game file
        persistenceManager.updateGameFile(gameIndex, update.toString());

        //update of the player file
        String playerUpdate = playersConnected.get(currentPlayerIndex).getScore() + "\n" +
                Arrays.deepToString(playersConnected.get(currentPlayerIndex).getPlayerShelf()) + "\n";

        System.out.println("\nupdate of "+playersConnected.get(currentPlayerIndex).getNickname());
        System.out.println(Arrays.deepToString(playersConnected.get(currentPlayerIndex).getPlayerShelf()));
        System.out.println();

        persistenceManager.updatePlayerFile(playersConnected.get(currentPlayerIndex).getNickname(), playerUpdate);

    }
    /**
     * This method manages the reconnection of the players after the server's connection drop
     * @param playerIndex: index of the player reconnecting
     * @author Irene Lo Presti
     */
    public void manageReconnectionPersistence(int playerIndex){
        if(clientHandlers.stream().filter(ClientHandler::isConnected).toList().size()==1)
            startCountdown(playerIndex);

        else if(playerIndex == currentPlayerIndex){
            checkIfStartTurnOrEndTheGame();
        }
    }

    /**
     * This method deletes the player's file
     * @param playerNickname: nickname of the player
     * @author Irene Lo Presti
     */
    public void fileDeleting(String playerNickname){
        //deleting player file
        persistenceManager.deletePlayerFile(playerNickname);
        numberOfPlayers--;
        if(numberOfPlayers==0)
            persistenceManager.deleteGameFile(gameIndex, false);
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
    public PersonalGoalDeck getPersonalDeck(){
        return personalDeck;
    }

}
