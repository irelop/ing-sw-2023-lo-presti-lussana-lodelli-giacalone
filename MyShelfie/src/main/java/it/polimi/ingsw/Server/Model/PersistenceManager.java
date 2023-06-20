package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RMIClientHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage the files for the FA persistence
 * @author Irene Lo Presti
 */

public class PersistenceManager {
    private String path;
    private ArrayList<File> gameFiles;
    private ArrayList<File> playersFiles;


    public PersistenceManager(){
        this.path = "src/safetxt/";
        this.gameFiles = new ArrayList<>();
        this.playersFiles = new ArrayList<>();

    }


    /**
     * Given the name, this method creates a new file for a new game and adds it to the array list
     * @param name of the new file: game_indexOfTheGame.txt
     */
    public void addNewGameFile(String name){
        File file = new File(path+name);
        try {
            if((file.exists() && file.length()==0 ) || file.createNewFile())
                this.gameFiles.add(file);
        } catch (IOException e) {
            System.out.println("Problems with file for server persistence creation.");
        }
    }

    /**
     * Given the name, this method creates a new file for a new player and adds it to the array list
     * @param nickname of the new player
     * @param gameIdx index of the player's game to be written in the file
     */
    public void addNewPlayerFile(String nickname, int gameIdx){
        String playerPath = getPlayerPath(nickname);
        File file = new File(playerPath);
        try {
            if(file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(gameIdx + "\n"); //write the index of the game in the player's file
                bw.flush();
                bw.close();
                this.playersFiles.add(file);
            }
        } catch (IOException e) {
            System.out.println("Problems with file for server persistence creation.");
        }

    }

    /**
     * This method deletes the file of the player whose nickname is passed as a parameter and removes it from
     * the array list
     * @param nickname of the player
     */
    public void deletePlayerFile(String nickname){
        String playerPath = getPlayerPath(nickname);
        File file = new File(playerPath);
        int index = playersFiles.indexOf(file);
        boolean success = file.delete();
        if(success)
            playersFiles.remove(index);
        else System.out.println("Problems deleting "+nickname+"'s persistence file");
    }

    /**
     * Getter method
     * @param index of the game
     * @return the file of the game at position 'index'
     */
    public File getGameFile(int index){
        return gameFiles.get(index);
    }

    /**
     * This method deletes the file of the game with index 'index' and removes it from
     * the array list
     * @param index of the game
     * @param removeFromList if true the method remove the game from the array list, otherwise it sets it null
     *                       to preserve the enumeration
     */
    public void deleteGameFile(int index, boolean removeFromList){
        boolean success = gameFiles.get(index).delete();
        if(success){
            if(removeFromList)
                gameFiles.remove(index);
            else gameFiles.set(index,null);
        }
        else System.out.println("Problems deleting file");

    }
    /**
     * FA: persistence. This method finds all the persistence file, and it saves them into the arraylist
     * @return the number of the old games
     */
    public int reset(){
        int gameIdx = 0;

        while(true){
            File file = new File(getGamePath(gameIdx));
            if (file.exists()){
                if(file.length()==0)
                    file.delete();
                else{
                    gameFiles.add(file);
                    gameIdx++;
                }
            }
            else break;
        }
        return gameIdx;
    }

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

        //read players names
        String[] playerNicknames = new String[numberOfPlayers];
        for(int j=0; j<numberOfPlayers; j++){
            playerNicknames[j] = ReadFileByLines.getLineByIndex(j+8);
        }

        //create a new game setting all the old info
        MyShelfie game = new MyShelfie(this, gameIdx, board, commonGoalCardsNames,
                currentPlayerIndex, isStarted, isOver, numberOfPlayers);

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

    public void updateGameFile(int index, String update){
        try {
            FileWriter fw = new FileWriter(gameFiles.get(index));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(update);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePlayerFile(String nickname, String update){
        ArrayList<String> staticInfo = getPlayerStaticInfo(nickname);

        String playerPath = getPlayerPath(nickname);

        FileWriter fw = null;
        try {
            fw = new FileWriter(playerPath);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String info : staticInfo){
                bw.write(info + "\n");
            }
            bw.write(update);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error during player file updating");
        }
    }

    private ArrayList<String> getPlayerStaticInfo(String nickname){
        String playerPath = getPlayerPath(nickname);

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom(playerPath);
        String line;
        ArrayList<String> info = new ArrayList<>();
        while((line = ReadFileByLines.getLine())!=null){
            info.add(line);
        }

        //remove the dynamic info
        info.remove(info.size()-1);
        info.remove(info.size()-1);

        return info;
    }

    private String getPlayerPath(String nickname){
        return path + nickname + ".txt";
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

    private String getGamePath(int gameIndex){
        return path + "game_" + gameIndex + ".txt";
    }

    public String setPlayer(Player player){
        File playerFile = new File(getPlayerPath(player.getNickname()));
        ReadFileByLines reader;
        reader = new ReadFileByLines();

        String cardIdx = null;

        if (playerFile.exists()){
            reader.readFrom(getPlayerPath(player.getNickname()));
            //set the chair only if the player has it
            if (Boolean.parseBoolean(ReadFileByLines.getLineByIndex(1)))
                player.setChair();

            //get the personal card index (the right card will be set in mysheflie)
            cardIdx = ReadFileByLines.getLineByIndex(2);

            //set the old score
            int score = Integer.parseInt(ReadFileByLines.getLineByIndex(3));
            player.setScore(score);

            //set the old shelf
            Tile[][] shelf = new Tile[6][5];
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
        return cardIdx;
    }
}
