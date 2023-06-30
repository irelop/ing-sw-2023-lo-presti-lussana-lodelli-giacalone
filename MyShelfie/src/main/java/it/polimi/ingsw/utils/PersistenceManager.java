package it.polimi.ingsw.utils;

import it.polimi.ingsw.Server.Model.*;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RMIClientHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import it.polimi.ingsw.Server.controller.*;


/**
 * Class to manage the files for the FA persistence
 * @author Irene Lo Presti
 */
public class PersistenceManager {
    private final String path;
    private final String gamePath;
    private final String playerPath;

    /**
     * Constructor method
     */
    public PersistenceManager(){
        this.path = "safetxt/";


        this.gamePath = path + "games/";
        this.playerPath = path + "players/";
    }

    //- - - - - - - - - - - - - - - -| FILE MANAGEMENT |- - - - - - - - - - - - - - - - - - - -
    /**
     * Given the index of the new game, this method creates a new file for the new game
     * @param gameIdx index of the new game
     */
    public void createNewGameFile(int gameIdx){
        File file = new File(getGamePath(gameIdx));
        try {
            if(!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Problems with file for server persistence creation.");
        }
    }

    /**
     * Given the nickname, this method creates a new file for a new player
     * @param nickname of the new player
     * @param gameIdx index of the player's game to be written in the file
     */
    public void createNewPlayerFile(String nickname, int gameIdx){
        String playerPath = getPlayerPath(nickname);
        File file = new File(playerPath);
        try {
            if(!file.exists()){
                if (file.createNewFile()) {
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(gameIdx + "\n");//write the index of the game in the player's file
                    bw.flush();
                    bw.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Problems with file for server persistence creation.");
        }

    }

    /**
     * This method gets the correct path of the file of the game with index 'gameIndex'
     * @param gameIndex: index of the game
     * @return the path of the game's file
     */
    private String getGamePath(int gameIndex){
        return gamePath + "game_" + gameIndex + ".txt";
    }

    /**
     * This method gets the correct path of the file of the player with nickname 'nickname'
     * @param nickname of the player
     * @return the path of the player's file
     */
    private String getPlayerPath(String nickname){
        return playerPath + nickname + ".txt";
    }

    /**
     * This method deletes the file of the game with index 'index'
     * @param index of the game
     */
    public void deleteGameFile(int index){
        File file = new File(getGamePath(index));
        if(file.exists()){
            boolean success = file.delete();
            if(!success)
                System.out.println("Problems deleting game file number "+index);
        }
    }

    /**
     * This method deletes the file of the player whose nickname is passed as a parameter
     * @param nickname of the player
     */
    public void deletePlayerFile(String nickname){
        String playerPath = getPlayerPath(nickname);
        File file = new File(playerPath);
        if(file.exists()) {
            boolean success = file.delete();
            if (!success) System.out.println("Problems deleting " + nickname + "'s persistence file");
        }
    }
    //- - - - - - - - - - - - - - - -| UPDATE FILES METHODS |- - - - - - - - - - - - - - - - - - - -
    /**
     * This method updates the file of the game with index 'index' with the 'update'
     * @param index of the game
     * @param update with the new information
     */
    public void updateGameFile(int index, String update){
        try {
            File file = new File(getGamePath(index));
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(update);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method updates the file of the player, it reads the static info calling a method, and then it
     * adds the new info
     * @param nickname of the player
     * @param update new information
     */
    public void updatePlayerFile(String nickname, String update){

        ArrayList<String> staticInfo = readPlayerStaticInfo(nickname);

        String playerPath = getPlayerPath(nickname);

        FileWriter fw;
        try {
            fw = new FileWriter(playerPath);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String info : staticInfo)
                bw.write(info + "\n");
            bw.write(update);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error during player file updating");
        }
    }
    /**
     * This method writes on the player file the update info
     * @param staticInfo information that don't change during the game
     * @param nickname of the player who just played
     */
    public void writeStaticPlayerInfo(String nickname, String staticInfo){
        String playerPath = getPlayerPath(nickname);
        try {
            FileWriter fw = new FileWriter(playerPath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(staticInfo);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * This method reads the player file and keeps the static information (the ones that don't change
     * during the game) and removing the dynamic ones
     * @param nickname of the player
     * @return arraylist with the static info
     */
    private ArrayList<String> readPlayerStaticInfo(String nickname){
        //String playerPath = getPlayerPath(nickname);

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("safetxt/players/" +nickname+".txt");
        String line;
        ArrayList<String> info = new ArrayList<>();
        while((line = ReadFileByLines.getLine())!=null){
            info.add(line);
        }

        //remove the dynamic info
        info.remove(info.size()-1);
        info.remove(info.size()-1);
        info.remove(info.size()-1);
        info.remove(info.size()-1);
        info.remove(info.size()-1);
        info.remove(info.size()-1);

        return info;
    }

    //- - - - - - - - - - - - - - - -| RESET OLD GAMES METHODS |- - - - - - - - - - - - - - - - - -
    /**
     * This method finds all the persistence files, returns the indexes of the old games and deletes the
     * empty files (both players' and games')
     * @return arraylist of old games indexes
     */
    public ArrayList<Integer> reset(){
        //find all the names of the old games files that are in folder
        String[] gameFilesNames = new File("safetxt/games").list();
        ArrayList<Integer> gameIndexes = new ArrayList<>();

        assert gameFilesNames != null;
        for (String fileName : gameFilesNames) {
            File file = new File(path + "games/" + fileName);
            if (file.exists()) {
                if (file.length() == 0)
                    file.delete(); //if the file is empty
                else //save the index in the arraylist to be returned
                    gameIndexes.add(Integer.parseInt(fileName.replaceAll("game_", "").replaceAll(".txt", "")));
            }
        }

        //find players files and delete the empty ones
        String[]  playerFilesNames = new File("safetxt/players").list();
        assert playerFilesNames != null;
        for(String player : playerFilesNames){
            File file = new File(path + "players/" + player);
            if(file.exists() && file.length() <= 2)
                file.delete();
        }

        return gameIndexes;
    }

    /**
     * This method reads the file of the game with the index 'gameIdx' and creates a new game with this information
     * @param gameIdx : index of the game to read
     * @return a new MyShelfie with the old information
     */
    public MyShelfie readOldGame(int gameIdx){
        ReadFileByLines reader;
        reader = new ReadFileByLines();
        reader.readFrom(getGamePath(gameIdx));

        //read the board matrix
        Tile[][] boardMatrix = new Tile[9][9];
        String row = ReadFileByLines.getLineByIndex(0);
        String[] values = row.replaceAll("\\[", "")
                .replaceAll("]", "")
                .split(", ");
        int index = 0;
        for(int j=0; j<9; j++){
            for(int k=0; k<9; k++){
                boardMatrix[j][k] = Tile.valueOf(values[index]);
                index++;
            }
        }
        //create a new board
        Board board = new Board();
        //init the new board with the one in the right file
        board.initFromMatrix(boardMatrix);

        //read the bag
        Map<Tile,Integer> bag = new HashMap<>();
        row = ReadFileByLines.getLineByIndex(6);
        values = row.replaceAll("\\{", "")
                .replaceAll("}", "")
                .split(", ");
        for(String value : values){
            String[] tile = value.split("=");
            bag.put(Tile.valueOf(tile[0]), Integer.valueOf(tile[1]));
        }
        //set the correct bag in the board created above
        board.setBag(bag);

        //read common goal cards names
        String[] commonGoalCardsNames = new String[2];
        commonGoalCardsNames[0] = ReadFileByLines.getLineByIndex(1);
        commonGoalCardsNames[1] = ReadFileByLines.getLineByIndex(2);

        //read all the other infos
        int currentPlayerIndex = Integer.parseInt(ReadFileByLines.getLineByIndex(3));
        boolean isStarted = Boolean.parseBoolean(ReadFileByLines.getLineByIndex(4));
        int numberOfPlayers = Integer.parseInt(ReadFileByLines.getLineByIndex(5));
        boolean isOver = Boolean.parseBoolean(ReadFileByLines.getLineByIndex(7));
        boolean firstTurn = Boolean.parseBoolean(ReadFileByLines.getLineByIndex(8));
        int firstToFinish = Integer.parseInt(ReadFileByLines.getLineByIndex(9));

        //read common score available
        int firstScore = Integer.parseInt(ReadFileByLines.getLineByIndex(10));
        int secondScore = Integer.parseInt(ReadFileByLines.getLineByIndex(11));

        //read players names
        String[] playerNicknames = new String[numberOfPlayers];
        for(int j=0; j<numberOfPlayers; j++){
            playerNicknames[j] = ReadFileByLines.getLineByIndex(j+12);
        }



        //create a new game setting all the old info
        MyShelfie game = new MyShelfie(this, gameIdx, board, commonGoalCardsNames,
                currentPlayerIndex, isStarted, isOver, numberOfPlayers, firstTurn, firstToFinish,
                firstScore, secondScore);

        //setting facade client handlers (they could also be socket) instantiates the players and
        // to check the connection
        //when a player reconnects to the game (like the FA client resilience) the facade client handler
        //will be replaced with the new one (working)
        for(String player : playerNicknames) {
            ClientHandler clientHandler = new RMIClientHandler(null);
            clientHandler.setIsConnected(false);
            game.addPlayer(player, clientHandler);
        }

        return game;
    }

    /**
     * This method sets the old player's info in the new instance of Player
     * @param player: new instance of Player
     * @return the id of the personal card of 'player' that will be set correctly by the controller
     */
    public void setPlayer(Player player, PersonalGoalDeck deck){
        File playerFile = new File(getPlayerPath(player.getNickname()));
        ReadFileByLines reader;
        reader = new ReadFileByLines();

        if (playerFile.exists() && playerFile.length() > 2){
            reader.readFrom(getPlayerPath(player.getNickname()));
            //set the chair only if the player has it
            if (Boolean.parseBoolean(ReadFileByLines.getLineByIndex(1)))
                player.setChair();

            //get the personal card
            String cardIdx = ReadFileByLines.getLineByIndex(2);
            int personalScore =  Integer.parseInt(ReadFileByLines.getLineByIndex(8));
            PersonalGoalCard personalGoalCard = deck.getCard(cardIdx);
            player.setCard(personalGoalCard);

            //set the old score
            int score = Integer.parseInt(ReadFileByLines.getLineByIndex(3));
            player.setScore(score);

            //set the old shelf
            Tile[][] shelf = new Tile[6][5];
            String row = ReadFileByLines.getLineByIndex(4);

            boolean hasFinished = Boolean.parseBoolean(ReadFileByLines.getLineByIndex(5));
            if(hasFinished)
                player.setHasFinished(true);
            boolean isFirstCommonAchieved = Boolean.parseBoolean(ReadFileByLines.getLineByIndex(6));
            if(isFirstCommonAchieved)
                player.setCommonGoalAchieved(0);
            boolean isSecondCommonAchieved = Boolean.parseBoolean(ReadFileByLines.getLineByIndex(7));
            if(isSecondCommonAchieved)
                player.setCommonGoalAchieved(1);

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
                //set the correct score
                personalGoalCard.getPersonalGoalScore(shelf);
            }
        }
    }
}
