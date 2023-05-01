package it.polimi.ingsw.Server.Model;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files


public class ReadFileByLines {
    private static ArrayList<String> text = null;

    public ReadFileByLines() {
        text = new ArrayList<>();
    }

    public static String getLine() {
        return text.remove(0);
    }
    public static String getLineByIndex(int index) {
        return text.get(index);
    }

    /**
     * Scan all the file line by line and put them into text
     * @param fileName: the name of the path to the file
     */
    public void readFrom(String fileName) {

        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                text.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can't find the file");
        }
    }
}
