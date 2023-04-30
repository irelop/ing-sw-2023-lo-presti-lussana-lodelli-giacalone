package it.polimi.ingsw.Server.Model;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.GoalAndScoreMsg;
import it.polimi.ingsw.Server.Messages.MyShelfMsg;
import it.polimi.ingsw.Server.Messages.ScoreBoardMsg;
import it.polimi.ingsw.Server.Messages.YourTurnMsg;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.Server.Model.Exceptions.NotEnoughSpaceInChosenColumnException;

import java.util.ArrayList;
import java.util.Random;

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
    private boolean started;
    private ArrayList<ClientHandler> clientHandlers;
    private int currentPlayerIndex;
    private static MyShelfie myShelfieInstance;

    public MyShelfie(){
        this.board = Board.getBoardInstance();
        this.commonDeck = new CommonGoalDeck();
        this.personalDeck = new PersonalGoalDeck();
        this.isOver = false;
        this.started = false;
        this.playersConnected = new ArrayList<>();
        this.numberOfPlayers = -1;
        this.clientHandlers = new ArrayList<>();
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

    //edit ANDREA: ho gestito lato view i casi in cui l'input non sia valido con
    // annessa stampa all'utente quindi direi ok.
    public void setNumberOfPlayers(int numberOfPlayers) {
        if(numberOfPlayers == -1){
            this.numberOfPlayers = numberOfPlayers;
            board.initGridParabolic(numberOfPlayers);
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
    }//dobbiamo farla vedere al player!

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
    }//le facciamo vedere ai giocatori????

    /**
     * OVERVIEW: this method manages the game: a player can play his/her turn if the match is not over
     *       or if that is the last lap
     */
    public void manageTurn(){
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
                    synchronized(this){
                        turn();
                        try {
                            this.wait();
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
        //dobbiamo creare un messaggio per far vedere a tutti i giocatori la EndgameView
        ArrayList<Integer> scoreList = new ArrayList<Integer>();
        for (Player player : playersConnected) {
            scoreList.add(playersConnected.get(currentPlayerIndex).myScore.getScore());
        }
        ScoreBoardMsg scoreBoardMsg = new ScoreBoardMsg(playersConnected, scoreList);

        for (Player player : playersConnected) {
            clientHandlers.get(currentPlayerIndex).sendMessageToClient(scoreBoardMsg);
        }
    }

    /**
     * OVERVIEW: it finds max pickable tiles by the player and creates a message to send to
     * ChooseTilesFromBoardView
     */
    private void turn() {

        // find max pickable tiles by the player
        int maxTilesPickable = playersConnected.get(currentPlayerIndex).myShelfie.maxTilesPickable();

        YourTurnMsg yourTurnMsg;
        yourTurnMsg = new YourTurnMsg(playersConnected.get(currentPlayerIndex).getNickname(), maxTilesPickable, Board.getBoardGrid());
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
            playersConnected.get(currentPlayerIndex).myScore.addScore(personalPointsEarned());
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
    }

    public void getPlayerChoice(int initialRow, int initialColumn, char direction, int numberOfTiles){
        board.pickTilesFromBoard(initialRow, initialColumn, numberOfTiles, direction, playersConnected.get(currentPlayerIndex));
        MyShelfMsg myShelfMsg = new MyShelfMsg(playersConnected.get(currentPlayerIndex).myShelfie.getGrid(), playersConnected.get(currentPlayerIndex).getLittleHand());
        clientHandlers.get(currentPlayerIndex).sendMessageToClient(myShelfMsg);
    }

    public void insertingTiles(int columnIdx,int[] orderIdxs) throws InvalidTileIndexInLittleHandException, NotEnoughSpaceInChosenColumnException {
        playersConnected.get(currentPlayerIndex).getTiles(orderIdxs);
        playersConnected.get(currentPlayerIndex).myShelfie.insert(columnIdx,playersConnected.get(currentPlayerIndex).getLittleHand());
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
        this.notify();
    }

}
