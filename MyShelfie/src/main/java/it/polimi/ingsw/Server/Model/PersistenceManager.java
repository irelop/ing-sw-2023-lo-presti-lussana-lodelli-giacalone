package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.Model.ReadFileByLines;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Integer.valueOf;

public class PersistenceManager {
    private String path;
    private ArrayList<File> gameFiles;
    private ArrayList<File> playersFiles;

    public PersistenceManager(){
        this.path = "src/safetxt/";
        this.gameFiles = new ArrayList<>();
        this.playersFiles = new ArrayList<>();
    }


    public void addNewGameFile(String name){
        File file = new File(path+name);
        try {
            if(file.createNewFile())
                this.gameFiles.add(file);
        } catch (IOException e) {
            System.out.println("Problems with file for server persistence creation.");
        }
    }
    public void addNewPlayerFile(String name, int controllerIdx){
        File file = new File(path+name);
        try {
            if(file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(controllerIdx + "\n");
                bw.flush();
                bw.close();
                this.playersFiles.add(file);
            }
        } catch (IOException e) {
            System.out.println("Problems with file for server persistence creation.");
        }

    }

    public void deletePlayerFile(String nickname){
        File file = new File(path + nickname + ".txt");
        int index = playersFiles.indexOf(file);
        boolean success = playersFiles.get(index).delete();
        if(success)
            playersFiles.remove(index);
    }

    public File getGameFile(int index){
        return gameFiles.get(index);
    }

    public void deleteGameFile(int index, boolean removeFromList){
        boolean success = gameFiles.get(index).delete();
        if(success){
            if(removeFromList)
                gameFiles.remove(index);
            else gameFiles.set(index,null);
        }

    }

    public int reset(){
        int gameIdx = 0, gamesNum = 0;//serve??

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

                    String row = ReadFileByLines.getLineByIndex(7);
                    String[] playerNicknames = row.replaceAll("\\{", "")
                            .replaceAll("}", "")
                            .split(", ");
                    for (String player : playerNicknames) {
                        try {
                            File playerFile = new File(path + player);
                            if (playerFile.exists()) {
                                FileWriter fw = new FileWriter(file);
                                BufferedWriter bw = new BufferedWriter(fw);
                                bw.write(gamesNum + "\n");
                                bw.flush();
                                bw.close();
                                this.playersFiles.add(playerFile);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    gameIdx++;
                }
            }
            else break;
        }
        return gamesNum;
    }

}
