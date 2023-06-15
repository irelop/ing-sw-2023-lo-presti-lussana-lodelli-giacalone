package it.polimi.ingsw.Server.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
     * @param name of the new file: playerNickname.txt
     * @param gameIdx index of the player's game to be written in the file
     */
    public void addNewPlayerFile(String name, int gameIdx){
        File file = new File(path+name);
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
        File file = new File(path + nickname + ".txt");
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
        int gameIdx = 0, gamesNum = 0;

        while(true){
            String pathFile = path + "game_" + gameIdx + ".txt";
            File file = new File(pathFile);
            if (file.exists()){
                if(file.length()==0)
                    file.delete();
                else{
                    gameFiles.add(file);
                    gamesNum++;

                    ReadFileByLines reader = new ReadFileByLines();
                    reader.readFrom(pathFile);

                    int playersNum = Integer.parseInt(ReadFileByLines.getLineByIndex(5));
                    for(int j=0; j<playersNum; j++){
                        File playerFile = new File(path+ReadFileByLines.getLineByIndex(j+8)+".txt");
                        if(playerFile.exists())
                            playersFiles.add(playerFile);
                    }
                    gameIdx++;
                }
            }
            else break;
        }
        return gamesNum;
    }

}
