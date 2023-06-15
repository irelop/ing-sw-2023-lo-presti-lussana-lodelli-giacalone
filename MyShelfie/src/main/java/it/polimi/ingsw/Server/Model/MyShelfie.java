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
    private ChatManager chatManager;

    //- - - R M I - - - - -
    private File persistenceFile;
    private String safeFilePath;

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
        this.safeFilePath = "src/safetxt/";
        this.chatManager = new ChatManager();
    }//non penso serva più

    /**
     * Constructor for new games
     * @param persistenceFile is the file where all the info of this game are going to be saved
     * @author Irene Lo Presti
     */
    public MyShelfie(File persistenceFile){
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
        this.persistenceFile = persistenceFile;
        this.safeFilePath = "src/safetxt/";
        this.chatManager = new ChatManager();
    }

    public boolean isGameOver(){
        return gameOver;
    }

    /**
     * Constructor for old games to be restored after the server crash
     * @param persistenceFile: file with all the info of this game
     * @param board: old board
     * @param commonGoalCardsNames: codes of the old common goal cards
     * @param currentPlayerIndex: index of the player that was playing the last turn
     * @param isStarted: boolean to know if the game was started
     * @param isOver: boolean to know if the games was finishing
     * @param numberOfPlayers: number of players connected to the old game
     *
     * @author Irene Lo Presti
     */
    public MyShelfie(File persistenceFile, Board board, String[] commonGoalCardsNames, int currentPlayerIndex, boolean isStarted, boolean isOver, int numberOfPlayers){
        this.commonDeck = new CommonGoalDeck();
        this.board = board;
        CommonGoalCard[] commonGoalCards = new CommonGoalCard[2];
        //get the right cards form the deck
        commonGoalCards[0] = commonDeck.getCard(commonGoalCardsNames[0]);
        commonGoalCards[1] = commonDeck.getCard(commonGoalCardsNames[1]);
        board.setCommonGoalCards(commonGoalCards);

        this.persistenceFile = persistenceFile;
        this.isOver = isOver;
        this.isStarted = isStarted;
        this.numberOfPlayers = numberOfPlayers;

        //initialize all the others attributes
        this.gameOver = false;
        this.lock = new Object();
        this.allPlayersReady = false;//?
        this.personalDeck = new PersonalGoalDeck();
        this.playersConnected = new ArrayList<>();
        this.clientHandlers = new ArrayList<>();
        this.safeFilePath = "src/safetxt/";
        this.chatManager = new ChatManager();
    }

    /**
     * Method for the FA: persistence. It reads the files of the players connected to the old game and sets
     * all the right info
     * @author Irene Lo Presti
     */
    public void resetPlayers(){
        String path;
        int numberOfPlayersConnected = 0;
        for(Player player : playersConnected){
            path = safeFilePath + player.getNickname()+".txt";
            ReadFileByLines reader;
            reader = new ReadFileByLines();
            File file = new File(path);
            if(file.exists()){
                numberOfPlayersConnected ++;
                reader.readFrom(path);

                //set the chair only if the player has it
                boolean hasChair = Boolean.parseBoolean(ReadFileByLines.getLineByIndex(1));
                if (hasChair)
                    player.setChair();

                //get the right personal card from the deck
                PersonalGoalCard card = personalDeck.getCard(ReadFileByLines.getLineByIndex(2));
                player.setCard(card);

                //set the old score
                int score = Integer.parseInt(ReadFileByLines.getLineByIndex(3));
                player.setScore(score);

                //set the old shelf
                Tile[][] shelf = new Tile[9][9];
                String row = ReadFileByLines.getLineByIndex(4);

                //if row.equals("shelf") the player didn't play their turn, so the shelf was all blank
                if (!row.equals("shelf")) {
                    String[] values = row.replaceAll("\\[", "")
                            .replaceAll("]", "")
                            .split(", ");
                    int index = 0;
                    for (int j = 0; j < 6; j++) {
                        for (int k = 0; k < 5; k++) {
                            shelf[j][k] = Tile.valueOf(values[index]);
                            index++;
                        }
                    }
                    player.setShelf(shelf);
                }
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
        try {
            FileWriter fw = new FileWriter(persistenceFile);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(update.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //update of the player file
        String playerPath = safeFilePath + playersConnected.get(currentPlayerIndex).getNickname() + ".txt";

        //read all the info to keep the "static" ones: controller number, personal goal card
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom(playerPath);
        String line;
        ArrayList<String> lines = new ArrayList<>();
        while((line = ReadFileByLines.getLine())!=null){
            lines.add(line);
        }

        //set the new score
        lines.set(lines.size()-2, String.valueOf(playersConnected.get(currentPlayerIndex).getScore()));

        //set the update shelf
        lines.set(lines.size()-1, Arrays.deepToString(playersConnected.get(currentPlayerIndex).getPlayerShelf()));

        //rewrite the file
        FileWriter fw = null;
        try {
            fw = new FileWriter(playerPath);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String newLine : lines){
                bw.write(newLine + "\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error during player file updating");
        }


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
        return(!(new ArrayList<>(this.playersConnected.stream().map(Player::getNickname).collect(Collectors.toList()))).contains(insertedString));
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
            chatManager.addChatter(playerNickname);

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
            updatePersistenceFiles();
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
        ArrayList<String> lobbyPlayers = new ArrayList<>(playersConnected.stream().map(Player::getNickname).collect(Collectors.toList()));

        for (int i = 0; i<playersConnected.size();i++) {
            LobbyUpdateAnswer msg = new LobbyUpdateAnswer(lobbyPlayers, allPlayersReady);
            clientHandlers.get(i).sendMessageToClient(msg);
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
                clientHandlers.get(i).sendMessageToClient(
                        new GameIsEndingUpdateAnswer(gameOver, i, firstToFinish, players, hasFinished));
            }
        }

    }

    private void writeOnPlayersFiles(String info, String playerNickname){

        String fileName = safeFilePath + playerNickname + ".txt";
        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(info);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * OVERVIEW: this method calls the method drawPersonal for each player, so every player has his / her own
     * personal goal card
     * @see Player
     * @see PersonalGoalDeck
     */
    private void dealPersonalCards(){
        for(Player player : playersConnected) {
            PersonalGoalCard card = personalDeck.drawPersonal();
            player.setCard(card);
            String info = card.getId() + "\n0\nshelf\n";
            writeOnPlayersFiles(info, player.getNickname());
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

        for(Player player : playersConnected){
            String info = player.hasChair()+"\n";
            writeOnPlayersFiles(info, player.getNickname());
        }

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
                shelfSnapshot,
                isOver
        );

        if (isOver) {
            //se isOver = true allora siamo all'ultima mano

            //settiamo a true il booleano del giocatore precedente
            if (currentPlayerIndex == 0)
                playersConnected.get(numberOfPlayers - 1).setHasFinished(true);
            else
                playersConnected.get(currentPlayerIndex - 1).setHasFinished(true);
            //aggiorniamo la game is ending view
            updateGameIsEndingView();
        }

        //qui viene mandata la view per la scelta tessere al current player

        if(clientHandlers.get(currentPlayerIndex).isConnected()){
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(yourTurnMsg);
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

        if(playersConnected.get(currentPlayerIndex).myShelfie.isShelfFull() &&  !isOver) {
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
            }
        }

        updatePersistenceFiles();

        GoalAndScoreMsg goalAndScoreMsg = new GoalAndScoreMsg(isCommonGoalAchived, isPersonalGoalAchived, playersConnected.get(currentPlayerIndex).myScore.getScore(), isShelfFull, isOver);
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

    public void setNextPlayer(){
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
                startCountdown(currentPlayerIndex);
                return; //?
            }

            //------------------------------------------------------------------------------------------------------

        checkIfStartTurnOrEndTheGame();

    }

    private void checkIfStartTurnOrEndTheGame(){
        if(!isOver || !playersConnected.get(currentPlayerIndex).hasChair()){
            if(firstTurn && currentPlayerIndex==0) {
                firstTurn = false;
            }
            startTurn();
        }
        //entered when everyone played last turn
        if(isOver && playersConnected.get(currentPlayerIndex).hasChair()){
            //spot check
            for (Player player : playersConnected) {
                int spotScore = player.myShelfie.spotCheck();
                player.myScore.addScore(spotScore);
            }

            //setting last player has finished
            if(currentPlayerIndex==0) {
                playersConnected.get(numberOfPlayers - 1).setHasFinished(true);
            }else {
                playersConnected.get(currentPlayerIndex - 1).setHasFinished(true);
            }

            //setting game over
            this.gameOver = true;

            updateGameIsEndingView();
        }
    }

    private void startCountdown(int playerIndex){
        LastOneConnectedMsg msg = new LastOneConnectedMsg(playersConnected.get(playerIndex).getNickname());
        clientHandlers.get(playerIndex).sendMessageToClient(msg);
    }

    public void shouldFinishTurn(ClientHandler clientHandler){
        //if the client disconnected was the actual one playing
        if(isStarted){
            if (clientHandlers.get(currentPlayerIndex).equals(clientHandler)) {
                computeCurrentPlayerIdx();
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

    public void persistenceManager(int playerIndex){
        if(clientHandlers.stream().filter(ClientHandler::isConnected).toList().size()==1)
            startCountdown(playerIndex);

        else if(playerIndex == currentPlayerIndex){
            checkIfStartTurnOrEndTheGame();
        }
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
            clientHandlers.get(playerIndex).sendMessageToClient(msg);
        }

    }

    public int checkPlayerDisconnected(String nickname){
        int found = -1;
        for(int i = 0; i< numberOfPlayers;i++){
            if(nickname.equals(playersConnected.get(i).getNickname()) && !clientHandlers.get(i).isConnected())
                found = i;
        }
        return found;
    }

    public void switchClientHandler(int playerIndex, ClientHandler clientHandlerReconnected){
        clientHandlerReconnected.setGame(this);
        clientHandlers.set(playerIndex, clientHandlerReconnected);
    }

    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
    public ClientHandler getLastClientHandler(){
        for(int i=0; i<numberOfPlayers; i++)
            if(clientHandlers.get(i).isConnected())
                return clientHandlers.get(i);
        return null;
    }
    public void finishGame(ClientHandler clientHandler){
        clientHandler.stop();

        int playerIndex = clientHandlers.indexOf(clientHandler);
        fileDeleting(playersConnected.get(playerIndex).getNickname());

    }

    public void finishGameRMI(RemoteInterface client){

        int playerIndex = -1;
        for(int i=0; i<playersConnected.size(); i++){
            if(clientHandlers.get(i) != null && clientHandlers.get(i).getIsRMI() && clientHandlers.get(i).getClientInterface().equals(client)){
                playerIndex = i;
                break;
            }
        }

        fileDeleting(playersConnected.get(playerIndex).getNickname());

    }

    public void fileDeleting(String playerNickname){
        //deleting player file
        File file = new File("src/safetxt/"+playerNickname+".txt");
        file.delete();

        numberOfPlayers--;
        if(numberOfPlayers==0){
            persistenceFile.delete();
        }
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public void getCustomChat(String requester){
        ChatStorage customChat = chatManager.getCustomChat(requester);
        if (!clientHandlers.get(currentPlayerIndex).getIsRMI())
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
        if(!clientHandlers.get(currentPlayerIndex).getIsRMI())
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(chatMsgAnswer);
        else {
            try {
                clientHandlers.get(currentPlayerIndex).getClientInterface().sendMessageToClient(chatMsgAnswer);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
