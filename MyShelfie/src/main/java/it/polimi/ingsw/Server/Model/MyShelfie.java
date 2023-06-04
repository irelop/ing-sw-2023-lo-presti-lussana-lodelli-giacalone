package it.polimi.ingsw.Server.Model;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.Server.Model.Exceptions.NotEnoughSpaceInChosenColumnException;
import it.polimi.ingsw.Server.RMIClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

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
    private boolean isStarted;
    private final ArrayList<ClientHandler> clientHandlers;
    private int currentPlayerIndex;
    private boolean allPlayersReady;
    private boolean firstTurn;
    private boolean gameOver;
    private final Object lock;
    private int firstToFinish;

    //- - - R M I - - - - -
    private ArrayList<Boolean> isRMIFirstLastLobby;

    public MyShelfie(){
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
        this.isRMIFirstLastLobby = new ArrayList<>();
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
     * @return true if the new player is the first one
     */
    public boolean isFirstConnected(){
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
            playersConnected.add(newPlayer);
            clientHandlers.add(clientHandler);
            isRMIFirstLastLobby.add(false);

            if (playersConnected.size() == numberOfPlayers && !this.allPlayersReady)
                this.allPlayersReady = true;
        }
    }

    public boolean getAllPlayersReady(){
        return allPlayersReady;
    }

    public void allPlayersReady(){
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
            startTurn();
        }
    }

    public boolean isStarted(){
        return this.isStarted;
    }


    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        if((playersConnected.size()==numberOfPlayers) && (!this.allPlayersReady)){
            this.allPlayersReady = true;
        }
    }

    public void manageLogin(ClientHandler clientHandler,LoginNicknameRequest loginNicknameRequest){
        S2CMessage loginNicknameAnswer;

        if (isStarted()) {
            loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.FULL_LOBBY);
            clientHandler.sendMessageToClient(loginNicknameAnswer);
            return;
        }


        if (checkNickname(loginNicknameRequest.getInsertedNickname())){

            if(isFirstConnected()){
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

    public void manageLoginRMI(LoginNicknameRequest msg, RemoteInterface client){
        S2CMessage loginNicknameAnswer;

        try {
            if (isStarted()) {
                loginNicknameAnswer = new LoginNicknameAnswer(msg, LoginNicknameAnswer.Status.FULL_LOBBY);
                client.sendMessageToClient(loginNicknameAnswer);
                return;
            }


            if (checkNickname(msg.getInsertedNickname())) {

                if (isFirstConnected()) {
                    loginNicknameAnswer = new LoginNicknameAnswer(msg, LoginNicknameAnswer.Status.FIRST_ACCEPTED);
                    client.sendMessageToClient(loginNicknameAnswer);
                    RMIClientHandler clientHandler = new RMIClientHandler(this, client);
                    addPlayer(msg.getInsertedNickname(), clientHandler);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } else {
                    loginNicknameAnswer = new LoginNicknameAnswer(msg, LoginNicknameAnswer.Status.ACCEPTED);
                    client.sendMessageToClient(loginNicknameAnswer);
                    RMIClientHandler clientHandler = new RMIClientHandler(this, client);
                    addPlayer(msg.getInsertedNickname(), clientHandler);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                }


            } else {
                loginNicknameAnswer = new LoginNicknameAnswer(msg, LoginNicknameAnswer.Status.INVALID);
                client.sendMessageToClient(loginNicknameAnswer);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }

    }

    public void updateLobby(){
        boolean lastRMIConnected = false;
        ArrayList<String> lobbyPlayers = new ArrayList<>(playersConnected.stream().map(x->x.getNickname()).collect(Collectors.toList()));

        for (int i = 0; i<playersConnected.size();i++) {
            if(!clientHandlers.get(i).getIsRMI()) {
                LobbyUpdateAnswer lobbyUpdateAnswer = new LobbyUpdateAnswer(lobbyPlayers, allPlayersReady);
                if(clientHandlers.get(i).isConnected())
                    clientHandlers.get(i).sendMessageToClient(lobbyUpdateAnswer);
            }
            else{
                if(i==(clientHandlers.size()-1)) lastRMIConnected = true;
                try {
                    LobbyUpdateAnswer lobbyUpdateAnswer = new LobbyUpdateAnswer(lobbyPlayers, allPlayersReady,lastRMIConnected);
                    if(clientHandlers.get(i).isConnected())
                        clientHandlers.get(i).getClientInterface().sendMessageToClient(lobbyUpdateAnswer);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

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
                if(!clientHandlers.get(i).getIsRMI()) {
                    clientHandlers.get(i).sendMessageToClient(
                            new GameIsEndingUpdateAnswer(gameOver, i, firstToFinish, players, hasFinished)
                    );
                }
                else{
                    try {
                        GameIsEndingUpdateAnswer msg;
                        if(isRMIFirstLastLobby.get(i)) {
                            msg = new GameIsEndingUpdateAnswer(gameOver, i, firstToFinish, players, hasFinished, true);
                            isRMIFirstLastLobby.set(i,false);
                        }else{
                            msg = new GameIsEndingUpdateAnswer(gameOver, i, firstToFinish, players, hasFinished);
                        }
                        clientHandlers.get(i).getClientInterface().sendMessageToClient(msg);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
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
     * OVERVIEW: it finds max pickable tiles by the current player and creates a message to send to
     * ChooseTilesFromBoardView
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

        yourTurnMsg = new YourTurnMsg(
                playersConnected.get(currentPlayerIndex).getNickname(),
                maxTilesPickable,
                boardSnapshot, Board.getCommonGoalCards(),
                playersConnected.get(currentPlayerIndex).getPersonalGoalCard(),
                firstTurn,
                playersNames,
                shelfSnapshot
        );

        if (isOver) {
            //se isOver = true allora siamo all'ultima mano

            //settiamo a true il booleano del giocatore precedente
            if (currentPlayerIndex == 0) {
                playersConnected.get(numberOfPlayers - 1).setHasFinished(true);
                isRMIFirstLastLobby.set(numberOfPlayers - 1, true);
            } else {
                playersConnected.get(currentPlayerIndex - 1).setHasFinished(true);
                isRMIFirstLastLobby.set(numberOfPlayers - 1, true);
            }

            //aggiorniamo la game is ending view
            updateGameIsEndingView();
        }
        //pensare come fare per fare un unico metodo isConnected() per controllare sia skt che rmi
        //qui viene mandata la view per la scelta tessere al current player

        if(clientHandlers.get(currentPlayerIndex).isConnected()){

            if (!clientHandlers.get(currentPlayerIndex).getIsRMI())
                clientHandlers.get(currentPlayerIndex).sendMessageToClient(yourTurnMsg);
            else{
                try {
                    clientHandlers.get(currentPlayerIndex).getClientInterface().sendMessageToClient(yourTurnMsg);
                }catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else{
            computeCurrentPlayerIdx();
            startTurn();
        }
    }

    //funzione chiamata dal process message del messaggio creato alla fine dell'inserimento delle
    //tessere nella shelf del giocatore
    public void computeTurnScore(){

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

        //parte dell'if è commentata per poter testare subito la fine di una partita
        //una volta risolti i problemi bisogna togliere il commento
        if( playersConnected.get(currentPlayerIndex).myShelfie.isShelfFull() &&  !isOver) {
            isShelfFull = true;
            playersConnected.get(currentPlayerIndex).myScore.addScore(1);
            this.isOver = true;

            this.firstToFinish = currentPlayerIndex;

            //se finisce un giocatore che non è il primo, settiamo che quelli prima di lui hanno
            // già fatto l'ultimo turno
            //es: se il giocatore 3 riempie la board, il giocatore 1 e il giocatore 2
            //      avranno già giocato il loro ultimo turno
            for(int i=0; i<=currentPlayerIndex; i++) {
                playersConnected.get(i).setHasFinished(true);
                isRMIFirstLastLobby.set(i,true);
            }
        }


        GoalAndScoreMsg goalAndScoreMsg = new GoalAndScoreMsg(isCommonGoalAchived, isPersonalGoalAchived, playersConnected.get(currentPlayerIndex).myScore.getScore(), isShelfFull, isOver);
        if(!clientHandlers.get(currentPlayerIndex).getIsRMI())
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(goalAndScoreMsg);
        else{
            try {
                clientHandlers.get(currentPlayerIndex).getClientInterface().sendMessageToClient(goalAndScoreMsg);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
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

        if(!clientHandlers.get(currentPlayerIndex).getIsRMI())
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(toShelfMsg);
        else{
            try {
                clientHandlers.get(currentPlayerIndex).getClientInterface().sendMessageToClient(toShelfMsg);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
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

   private void computeCurrentPlayerIdx(){
            if(currentPlayerIndex == numberOfPlayers-1)
                currentPlayerIndex = 0;
            else
                currentPlayerIndex ++;
    }

    public void finishTurn(){
            //setting the next player as the current player
            computeCurrentPlayerIdx();

            //skipping when a player is disconnected from the game (FA Resilienza alle disconessioni)
            int numOfPlayersConnected = numberOfPlayers;
            while(true){
                if(!clientHandlers.get(currentPlayerIndex).isConnected()){
                    computeCurrentPlayerIdx();
                    numOfPlayersConnected--;
                }
                else break;
            }

            if(numOfPlayersConnected == 1){
                LastOneConnectedMsg msg = new LastOneConnectedMsg(playersConnected.get(currentPlayerIndex).getNickname());
                if(!clientHandlers.get(currentPlayerIndex).getIsRMI())
                    clientHandlers.get(currentPlayerIndex).sendMessageToClient(msg);
                else{
                    try {
                        clientHandlers.get(currentPlayerIndex).getClientInterface().sendMessageToClient(msg);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                return;
            }

            //------------------------------------------------------------------------------------------------------

            if(!isOver || !playersConnected.get(currentPlayerIndex).hasChair()){
                if(firstTurn && currentPlayerIndex==0) {
                    firstTurn = false;
                }
                startTurn();
            }
            //entered when everyone played last turn
            if(isOver && playersConnected.get(currentPlayerIndex).hasChair()){
                spotCheck();
            }
    }

    public void shouldFinishTurn(ClientHandler clientHandler){
        //if the client disconnected was the actual one playing
        if(isStarted){
            if (clientHandlers.get(currentPlayerIndex).equals(clientHandler)) {
                if (currentPlayerIndex == 0)
                    currentPlayerIndex = numberOfPlayers - 1;
                else
                    currentPlayerIndex--;
                finishTurn();

            }
        }
    }

    public void spotCheck(){
        //spot check
        for (Player player : playersConnected) {
            int spotScore = player.myShelfie.spotCheck();
            player.myScore.addScore(spotScore);
        }

        //setting last player has finished
        if(currentPlayerIndex==0) {
            playersConnected.get(numberOfPlayers - 1).setHasFinished(true);
            isRMIFirstLastLobby.set(numberOfPlayers -1,true);
        }else {
            playersConnected.get(currentPlayerIndex - 1).setHasFinished(true);
            isRMIFirstLastLobby.set(numberOfPlayers - 1,true);
        }

        //setting game over
        this.gameOver = true;

        updateGameIsEndingView();
    }

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
            if(!clientHandlers.get(playerIndex).getIsRMI())
                clientHandlers.get(playerIndex).sendMessageToClient(msg);
            else{
                try {
                    clientHandlers.get(playerIndex).getClientInterface().sendMessageToClient(msg);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public boolean checkDisconnectedSocketClient(String nickname, ClientHandler clientHandlerReconnected){
        int found = -1;
        for(int i = 0; i< numberOfPlayers;i++){
            if(nickname.equals(playersConnected.get(i).getNickname()))
                found = i;
        }
        if(found == -1 || clientHandlers.get(found).isConnected()) return false;
        else{
            clientHandlerReconnected.setGame(this);
            clientHandlers.set(found, clientHandlerReconnected);
            return true;
        }
    }

    public boolean checkDisconnectedRMIClient(String nickname, RemoteInterface client){
        int found = -1;
        for(int i = 0; i< numberOfPlayers;i++){
            if(nickname.equals(playersConnected.get(i).getNickname()))
                found = i;
        }
        if(found == -1 || clientHandlers.get(found).isConnected()) return false;
        else{
            RMIClientHandler clientHandlerReconnected = new RMIClientHandler(this, client);
            clientHandlers.set(found, clientHandlerReconnected);
            return true;
        }
    }

    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    public void finishGame(ClientHandler clientHandler){
        clientHandler.stop();
    }

    public void finishGameRMI(RemoteInterface client){}


}
