package it.polimi.ingsw.Client.View;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidDirectionException;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidNumberOfTilesException;
import it.polimi.ingsw.Server.Messages.*;

import java.util.Scanner;

/**
 * View where the client chooses the initial position of the tiles that he/she wants to choose from the board
 * @author Irene Lo Presti
 */

//DA CANCELLARE
    /*
public class ChooseDirectionAndNumberOfTilesView extends View{

    public int MAX_COLUMNS = 9, MAX_ROWS = 9;
    private final ChooseDirectionAndNumberOfTilesMsg chooseDirectionAndNumberOfTilesMsg;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_LIGHTBLUE = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_PINK = "\u001B[35m";
    public static final String ANSI_BLACK = "\u001B[30m";


    /**
     * OVERVIEW: constructor method
     * @param chooseDirectionAndNumberOfTilesMsg :
     **//*
    public ChooseDirectionAndNumberOfTilesView(ChooseDirectionAndNumberOfTilesMsg chooseDirectionAndNumberOfTilesMsg){
        this.chooseDirectionAndNumberOfTilesMsg = chooseDirectionAndNumberOfTilesMsg;
    }

    /**
     * OVERVIEW: in this method the player chooses the numberOfTiles and the direction
     *//*
    @Override
    public void run(){

        printBoard();
        System.out.println("The initial position is marked with a star");

        int numberOfTiles;
        char direction;

        do{
            try{
                numberOfTiles = getNumberOfTiles();
                break;
            }catch(InvalidNumberOfTilesException e){
                System.out.println(e);
            }
        }while(true);

        if(numberOfTiles==0)
            direction = '0';
        else{
            do{
                try{
                    direction = getDirection();
                    break;
                }catch(InvalidDirectionException e){
                    System.out.println(e);
                }
            }while(true);
        }

        //PlayerChoiceMsg playerChoiceMsg = new PlayerChoiceMsg(direction, numberOfTiles, );
       // getOwner().getServerHandler().sendMessageToServer(playerChoiceMsg);

    }

    /**
     * OVERVIEW: this method prints the board and marks the initial position with a star
     *//*
    public void printBoard(){

        String code;

        //printing the indexes of the columns
        System.out.print("\u2716\t");
        for(int i=0; i<MAX_COLUMNS; i++)
            System.out.print((i+1)+"\t");
        System.out.println();

        for(int r=0; r<MAX_ROWS; r++){
            //printing the indexes of the rows
            System.out.print((r+1)+"\t");

            //printing the tiles
            for(int c=0; c<MAX_COLUMNS; c++){

                if(r == chooseDirectionAndNumberOfTilesMsg.initialRow-1 && c == chooseDirectionAndNumberOfTilesMsg.initialColumn-1)
                    code = "*";
                else
                    code = "\u25CF";

                switch (chooseDirectionAndNumberOfTilesMsg.boardSnapshot[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(ANSI_BLACK + code + ANSI_RESET);
                    case PINK -> System.out.print(ANSI_PINK + code + ANSI_RESET);
                    case GREEN -> System.out.print(ANSI_GREEN + code + ANSI_RESET);
                    case BLUE -> System.out.print(ANSI_BLUE + code + ANSI_RESET);
                    case LIGHTBLUE -> System.out.print(ANSI_LIGHTBLUE + code + ANSI_RESET);
                    case WHITE -> System.out.print(ANSI_WHITE + code + ANSI_RESET);
                    case YELLOW -> System.out.print(ANSI_YELLOW + code + ANSI_RESET);
                }
                System.out.print("\t");
            }
            System.out.println();
        }

    }

    /**
     * OVERVIEW: getter method for direction
     * @return direction (n, s, w or e)
     * @throws InvalidDirectionException if the direction is not n, s, w or e
     *//*
    private char getDirection() throws InvalidDirectionException {

        System.out.println(chooseDirectionAndNumberOfTilesMsg.nickname + ", now it's time to" +
                " insert the direction that you in which you want to choose those tiles.");
        System.out.println("Remember: you can chose between n (north), s (south), e (east), w (west).");
        System.out.print("Please insert the direction: ");

        Scanner scanner = new Scanner(System.in);
        char direction;
        direction = scanner.next().charAt(0);

        if(direction != 'n' && direction != 's' && direction != 'e' && direction != 'w')
            throw new InvalidDirectionException();

        else
            return direction;
    }

    /**
     * OVERVIEW: getter method for the number of tiles
     * @return number of tiles >= 0 && number of tiles < maxTilesPickable-1
     * @throws InvalidNumberOfTilesException if the number of tiles is not between 0 and axTilesPickable-1
     *//*
    private int getNumberOfTiles() throws InvalidNumberOfTilesException{

        System.out.println(chooseDirectionAndNumberOfTilesMsg.nickname + ", now it's time to" +
                " insert the number of tiles that you " +
                "want to chose (other than the one that you have already chose).");
        System.out.println("Remember: you can chose between 0 (if you don't want to pick others tiles)" +
                "and "+(chooseDirectionAndNumberOfTilesMsg.maxTilesPickable-1)+".");
        System.out.print("Please insert the number of tiles: ");

        Scanner scanner = new Scanner(System.in);
        int numberOfTiles;
        numberOfTiles = scanner.nextInt();

        if(numberOfTiles<0 || numberOfTiles>chooseDirectionAndNumberOfTilesMsg.maxTilesPickable-1)
            throw new InvalidNumberOfTilesException(chooseDirectionAndNumberOfTilesMsg.maxTilesPickable);

        else
            return numberOfTiles;
    }


}
*/