package it.polimi.ingsw.Server.controller;

import it.polimi.ingsw.Server.*;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import it.polimi.ingsw.utils.PersistenceManager;
import it.polimi.ingsw.utils.ReadFileByLines;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Class to manage multiple games
 */
public class GameRecord {
    //private ArrayList<MyShelfie> games;
    private HashMap<Integer, MyShelfie> games;
    private int currentGame;
    private RemoteInterface remoteServer;
    private final Object lock;
    private final PersistenceManager persistenceManager;
    private boolean persistenceManaged;


    /**
     * Constructor method
     */
    public GameRecord() {
        games = new HashMap<>();
        currentGame = -1;
        lock = new Object();
        persistenceManager = new PersistenceManager();
        persistenceManaged = false;
    }

    public void setRemoteServer(RemoteInterface remoteServer){
        this.remoteServer = remoteServer;
    }

    /**
     * This method checks if all players are connected to the current game, if not it returns the current game
     * else it adds a new game to the array list
     * @return current game or a new one
     * @author Lo Presti, Giacalone
     */
    public synchronized MyShelfie getGame(){
        if (currentGame == -1 || games.get(currentGame) == null || games.get(currentGame).getAllPlayersReady()) {
            do{
                currentGame++;
            }while(games.containsKey(currentGame));
            persistenceManager.addNewGameFile(currentGame);
            //MyShelfie game = new MyShelfie(persistenceManager.getGameFile(currentGame));
            MyShelfie game = new MyShelfie(persistenceManager, currentGame);
            games.put(currentGame, game);
        }
        return games.get(currentGame);
    }

    /**
     * This method delete the game passed as a parameter and the file of the player that called this method.
     * If the game is the current it removes it from the arrayList, else it doesn't remove it in order to
     * maintain the correct enumeration.
     * It calls the method in the persistence manager that delete the game file and also the player's file
     *
     * @param playerNickname: the player that called the function
     * @author Irene Lo Presti
     */
    public void deleteGame(int gameIndex, String playerNickname){
       // boolean remove = false;
        /*if(index == currentGame) {
            games.remove(game);
            currentGame--;
            remove = true;
        }
        else
            games.set(index, null);*/
        //remove = true;

        games.remove(gameIndex);
        currentGame = gameIndex;

        persistenceManager.deletePlayerFile(playerNickname);
        persistenceManager.deleteGameFile(gameIndex);
    }

    /**
     * This method is used for the FA persistence. After the server reconnects, this method recreates all the old
     * games: first it call the reset() function to recovery all the files, then it sets all the information from
     * the files in the games
     *
     * @author Irene Lo Presti
     */
    public void reset() {
        int[] gamesNum = persistenceManager.reset(); //find how many old games there were
        for(int i=0; i<gamesNum.length; i++){
            MyShelfie game = persistenceManager.readOldGame(i);
            game.resetPlayers();
            games.put(gamesNum[i],game);
        }
        persistenceManaged = true;
    }

    /**
     * FA: client resilience. This method is used to reconnect a player whose connection has dropped or
     * reconnects players after the server connection drop (FA: persistence)
     * @param nickname of the player who wants to reconnect
     * @param clientHandler of the player who wants to reconnect
     * @author Irene Lo Presti, Andrea Giacalone
     */
    public synchronized void reconnectPlayer(String nickname, ClientHandler clientHandler, boolean isGui){
        clientHandler.setIsGui(isGui);

        String msg = null;
        int gameIndex = -1, playerIndex = -1;
        ClientHandler countDownClient = null;

        String pathFile = "src/safetxt/players/"+nickname+".txt";
        File file = new File(pathFile);

        //check if exists a file with the name of the player
        if(!file.exists())
            msg = """

                    There isn't any disconnected player matching with your nickname.
                    Redirecting to a new lobby.
                    """;
        //file incomplete because the player didn't play any games
        else if(file.length()<=2) {
            msg = """

                    You were connected to a game not already started.
                    Redirecting to a new lobby.
                    """;
            persistenceManager.deletePlayerFile(nickname);
        }
        else{ //the file exists and it's complete
            ReadFileByLines reader = new ReadFileByLines();
            reader.readFrom(pathFile);

            //read the controller index from the player's file
            gameIndex = Integer.parseInt(ReadFileByLines.getLineByIndex(0));

            //find if the player is really disconnected from the game
            playerIndex = games.get(gameIndex).checkPlayerDisconnected(nickname);

            if(playerIndex == -1)
                msg = """
                        
                        The player with your nickname is connected and is playing.
                        Redirecting to a new lobby.
                        """;
            else{
                //find how many players are still connected to the game

                int playersConnected = games.get(gameIndex).getClientHandlers().stream().filter(ClientHandler::isConnected).toList().size();

                if(playersConnected == 0 && (!persistenceManaged || games.get(gameIndex).isGameOver())){
                    //if there aren't players connected we are in 2 possible scenarios:
                        //1) the server connection dropped so all players are reconnecting so the player can
                        //   reconnect to the game -> !persistenceManaged
                        //2) the player connection drop but the game is over
                    msg = """

                            Sorry, it took you too long to reconnect so the game is over.
                            Redirecting to a new lobby.""";

                    //games.get(gameIndex).fileDeleting(nickname);
                    deleteGame(gameIndex, nickname);
                }
                //if there are no players connected and persistenceManaged == true
                // then this player is the first one to reconnect to the game

                else{
                    if(playersConnected == 1 && games.get(gameIndex).isPlayerInCountdown())
                        countDownClient = games.get(gameIndex).getCountdownClientHandler(); //the player who is in Countdown Mode
                    if(clientHandler.getIsRMI()){
                        try {
                            //set the correct controller in the map
                            remoteServer.resetControllerToClient(games.get(gameIndex), clientHandler.getClientInterface());
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    games.get(gameIndex).switchClientHandler(playerIndex, clientHandler);
                }
            }
        }

        //send the result of the reconnection choice to the player
        S2CMessage reconnectionAnswer = new ReconnectionAnswer(msg,nickname);
        clientHandler.sendMessageToClient(reconnectionAnswer);

        if(countDownClient != null) {
            //stop the countdown
            countDownClient.sendMessageToClient(new ReconnectionNotifyMsg(nickname));
            //go on with the game
            games.get(gameIndex).setNextPlayer();
        }

        //if all players are reconnecting after the server crashed
        else if(persistenceManaged && msg == null)
            games.get(gameIndex).manageReconnectionPersistence(playerIndex);
    }

    /**
     * This method check the nickname of the new player and if it's valid, connects them to the game
     * @param clientHandler of the new player
     * @param loginNicknameRequest message with the new player's nickname
     * @param game where the new player is connecting
     * @author Andrea Giacalone, Irene Lo Presti
     */
    public synchronized void manageLogin(ClientHandler clientHandler, LoginNicknameRequest loginNicknameRequest,
                                         MyShelfie game, boolean isGui){
        clientHandler.setIsGui(isGui);
        S2CMessage loginNicknameAnswer;

        if (game.isStarted()) {
            loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.FULL_LOBBY);
            clientHandler.sendMessageToClient(loginNicknameAnswer);
            return;
        }

        String pathFile = "src/safetxt/players/"+loginNicknameRequest.getInsertedNickname()+".txt";
        File file = new File(pathFile);

        if (!file.exists()) {

            if (game.isFirstConnected()) {
                loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.FIRST_ACCEPTED);
            }

            else {
                loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.ACCEPTED);
            }

            game.addPlayer(loginNicknameRequest.getInsertedNickname(), clientHandler);
            int controllerIdx = -1;
            for(int i=0; i<games.size(); i++){
                if(games.get(i)!=null && games.get(i).equals(game)) {
                    controllerIdx = i;
                    break;
                }
            }
            persistenceManager.addNewPlayerFile(loginNicknameRequest.getInsertedNickname(), controllerIdx);
            clientHandler.sendMessageToClient(loginNicknameAnswer);

        } else {
            loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.INVALID);
            clientHandler.sendMessageToClient(loginNicknameAnswer);
        }

    }
}
