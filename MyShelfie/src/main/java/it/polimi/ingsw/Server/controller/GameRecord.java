package it.polimi.ingsw.Server.controller;

import it.polimi.ingsw.Server.*;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import it.polimi.ingsw.utils.PersistenceManager;
import it.polimi.ingsw.utils.ReadFileByLines;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to manage multiple games
 */
public class GameRecord {
    private HashMap<Integer, MyShelfie> games;
    private int currentGame;
    private RemoteInterface remoteServer;
    private final PersistenceManager persistenceManager;
    private final String playersPath;


    /**
     * Constructor method
     */
    public GameRecord() {
        games = new HashMap<>();
        currentGame = -1;
        persistenceManager = new PersistenceManager();
        playersPath = "src/safetxt/players/";
    }

    /**
     * This method checks if all players are connected to the current game, if not it returns the current game
     * else it adds a new game to the hashmap, computing the correct index
     * @return current game or a new one
     * @author Irene Lo Presti, Andrea Giacalone
     */
    public synchronized MyShelfie getGame(){
        if (currentGame == -1 || games.get(currentGame) == null || games.get(currentGame).getAllPlayersReady()) {
            //after a server reconnection the indexes of the games could be not ordered, so
            // we have to find the first free index
            do{
                currentGame++;
            }while(games.containsKey(currentGame));

            MyShelfie game = new MyShelfie(persistenceManager, currentGame);
            persistenceManager.createNewGameFile(currentGame);
            games.put(currentGame, game);
        }
        return games.get(currentGame);
    }

    /**
     * This method deletes the game passed as a parameter and the file of the player that called this method.
     * It calls the method in the persistence manager that delete the game's file
     * @author Irene Lo Presti
     */
    public void deleteGame(int gameIndex){
        games.remove(gameIndex);
        currentGame = gameIndex;
        persistenceManager.deleteGameFile(gameIndex);
    }

    /**
     * This method is used for the FA persistence. After the server reconnects, this method recreates all the old
     * games: first it call the reset() function to recover all the files then resets the old games
     *
     * @author Irene Lo Presti
     */
    public void reset() {
        ArrayList<Integer> gamesIndexes = persistenceManager.reset(); //find the indexes of the old games
        for(Integer index : gamesIndexes){
            MyShelfie game = persistenceManager.readOldGame(index);
            game.resetPlayers();
            games.put(index, game);
        }
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

        String pathFile = playersPath + nickname + ".txt";
        File file = new File(pathFile);

        //check if exists a file with the name of the player
        if(!file.exists())
            msg = """

                    There isn't any disconnected player matching with your nickname.
                    Redirecting to a new lobby.
                    """;
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

                if(playersConnected == 0 && (!games.get(gameIndex).isPersistenceManaged() || games.get(gameIndex).isGameOver())){
                    //if there aren't players connected we are in 2 possible scenarios:
                        //1) the server connection dropped so all players are reconnecting so the player can
                        //   reconnect to the game -> !persistenceManaged
                        //2) the player connection drop but the game is over
                    msg = """

                            Sorry, it took you too long to reconnect so the game is over.
                            Redirecting to a new lobby.""";

                    //games.get(gameIndex).fileDeleting(nickname);
                    deleteGame(gameIndex);
                    persistenceManager.deletePlayerFile(nickname);
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
            if(!games.get(gameIndex).isPersistenceManaged())
                games.get(gameIndex).setNextPlayer();
        }

        //if all players are reconnecting after the server crashed
        if(msg == null && games.get(gameIndex).isPersistenceManaged())
            games.get(gameIndex).manageReconnectionPersistence(playerIndex, countDownClient != null);
    }

    /**
     * This method checks the nickname of the new player and if it's valid, connects them to the game
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

        String pathFile = playersPath + loginNicknameRequest.getInsertedNickname() + ".txt";
        File file = new File(pathFile);

        if (!file.exists()) {

            if (game.isFirstConnected())
                loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.FIRST_ACCEPTED);
            else
                loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.ACCEPTED);

            game.addPlayer(loginNicknameRequest.getInsertedNickname(), clientHandler);

            persistenceManager.createNewPlayerFile(loginNicknameRequest.getInsertedNickname(), getGameIndex(game));
            clientHandler.sendMessageToClient(loginNicknameAnswer);

        } else {
            loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.INVALID);
            clientHandler.sendMessageToClient(loginNicknameAnswer);
        }

    }

    private int getGameIndex(MyShelfie game){
        int gameIdx = -1;
        for(int i=0; i<games.size(); i++){
            if(games.get(i)!=null && games.get(i).equals(game)) {
                gameIdx = i;
                break;
            }
        }
        return gameIdx;
    }
    public void setRemoteServer(RemoteInterface remoteServer){
        this.remoteServer = remoteServer;
    }
}
