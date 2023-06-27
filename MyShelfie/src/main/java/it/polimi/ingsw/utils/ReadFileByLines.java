package it.polimi.ingsw.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files

/**
 * ReadFileByLines class: this utility class allows to read formatted txt files and to read them line by line in order
 * to properly initialize data structures.
 * @author Riccardo Lodelli
 */
public class ReadFileByLines {
    private static ArrayList<String> text = null;

    public ReadFileByLines() {
        text = new ArrayList<>();
    }

    public static String getLine() {
        if(text.size()==0)
            return null;
        return text.remove(0);
    }
    public static String getLineByIndex(int index) {
        return text.get(index);
    }

    /**
     * Scan all the file line by line and put them into text attribute
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

        } catch (Exception e) {
            System.out.println("Error in reading the file");
        }
    }
    public void readFromResource(InputStream input) {

        try {
            if(input!=null){
                Scanner myReader = new Scanner(input);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    text.add(data);
                }
                myReader.close();
            }else{
                System.out.println("Can't find the file");
            }

        } catch (Exception e) {
            System.out.println("Error in reading the file");
        }
    }
}
