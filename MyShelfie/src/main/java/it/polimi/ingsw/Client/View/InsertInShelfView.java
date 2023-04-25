package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Model.Exceptions.InvalidShelfColumnException;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.Server.Model.Tile;
import it.polimi.ingsw.Server.Messages.InsertingTilesMsg;
import it.polimi.ingsw.Server.Messages.MyShelfMsg;

import java.util.ArrayList;
import java.util.Scanner;

// prima bozza

/**
 * This view is shown when the current player has to insert the tiles he/she took from the board into
 * his/her personal shelf. The player interacts with this view to choose the column he/she wants to fill
 * with tiles and to put in order the tiles that must be placed in the shelf
 *
 * @author Riccardo Lodelli
 */

public class InsertInShelfView extends View {


    // colors to stamp the shelf
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_LIGHTBLUE = "\u001B[36m";
    public static final String ANSI_YELLOW = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_PINK = "\u001B[35m";
    public static final String ANSI_BLACK = "\u001B[30m";

    private final MyShelfMsg msg;

    public InsertInShelfView(MyShelfMsg msg){
        this.msg = msg;
    }

    /**
     * This method prints the player shelf to help him to decide what to do
     * @param myShelf: the shelf which has to be printed
     */
    public void printShelf(Tile[][] myShelf) {
        String circle = "\u25CF";

        // Printing column's indexes...
        System.out.print("\u2798" + "\t");
        for (int i = 0; i < myShelf[0].length; i++)
            System.out.print( (i+1) + "\t" );
        System.out.println();

        for (int i = 0; i < myShelf.length; i++) {
            // Printing row's indexes...
            System.out.print( (i+1) + "\t" );
            // Printing the shelf...
            for (int j = 0; j < myShelf[0].length; j++) {
                switch (myShelf[i][j]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(ANSI_BLACK + circle + ANSI_RESET);
                    case PINK -> System.out.print(ANSI_PINK + circle + ANSI_RESET);
                    case GREEN -> System.out.print(ANSI_GREEN + circle + ANSI_RESET);
                    case BLUE -> System.out.print(ANSI_BLUE + circle + ANSI_RESET);
                    case LIGHTBLUE -> System.out.print(ANSI_LIGHTBLUE + circle + ANSI_RESET);
                    case WHITE -> System.out.print(ANSI_WHITE + circle + ANSI_RESET);
                    case YELLOW -> System.out.print(ANSI_YELLOW + circle + ANSI_RESET);
                }
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Manca la gestione delle eccezioni lato server

    /**
     * The main method of the view, overrided form thread run() method
     * It sends an InsertingTilesMsg containing player's choises (if right)
     */
    @Override
    public void run(){

        int columnChosen;
        int[] chosenOrderIndexes;
        ArrayList<Tile> chosenTiles = msg.getLittleHand();
        Tile[][] myShelf = msg.getShelf();

        do{
            printShelf(myShelf);
            try{
                columnChosen = chooseColumn();
                break;
            }catch(InvalidShelfColumnException e){
                System.out.println(e);
            }
        }while(true);

        do{
            try{
                chosenOrderIndexes = askOrder(chosenTiles);
                break;
            }catch(InvalidTileIndexInLittleHandException e){
                System.out.println(e);
            }
        }while(true);

        InsertingTilesMsg insertingMsg = new InsertingTilesMsg(columnChosen, chosenOrderIndexes);
        getOwner().getServerHandler().sendMessageToServer(insertingMsg);

        // per passare alla Goal View bisogna usare il metodo processMessage in GoalAndScoreMsg
        //if (nextView != null)
        //    getOwner().transitionToView(nextView);
    }

    /**
     * This method allows the player to choose the column where he/she wants to place tiles
     * @return the index of the column chosen by the player
     * @throws InvalidShelfColumnException: thrown to avoid wrong indexes for the column
     */
    // Manca la gestione dell'eccezione "colonna piena"
    public int chooseColumn() throws InvalidShelfColumnException {
        int columnChosen;
        System.out.println("choose the Column:");
        Scanner scanner = new Scanner(System.in);
        columnChosen = scanner.nextInt();
        if (columnChosen < 0 || columnChosen >= 5)  throw new InvalidShelfColumnException();
        return columnChosen;
    }

    /**
     * This method allows the player to choose the order for the tiles he/she wants to place
     * @param chosenTiles: an array containing the tiles that the player has chosen from the board
     * @return an array of indexes which indicates the order wanted by the player
     * @throws InvalidTileIndexInLittleHandException: thrown to avoid wrong order indexes
     */
    public int[] askOrder(ArrayList<Tile> chosenTiles) throws InvalidTileIndexInLittleHandException {

        int tilesNumber = chosenTiles.size();
        int[] choices = new int[tilesNumber];
        boolean sameColors = true;

        if(chosenTiles.size()==1){
            choices[0] = 1;
            return choices;
        }

        for(int i=0;i<tilesNumber-1;i++){
            if (chosenTiles.get(i) != chosenTiles.get(i+1)) {
                sameColors = false;
                break;
            }
        }
        if(sameColors) {
            for(int i=0;i<tilesNumber;i++){
                choices[i] = i+1;
            }
            return choices;
        }

        System.out.println("You picked this tiles:");
        for(int i=0; i<tilesNumber; i++)
            System.out.println((i+1)+ ") " + chosenTiles.get(i));
        System.out.println("You can reorder them before inserting them in your shelf: \n" +
                "Please enter indexes in the order you want to insert tiles " +
                "(remember: first index will be the lowest in the shelf)");
        getTiles(choices);

        return choices;
    }


    /**
     * This private method is called by askOrder(). It takes player's inputs and check if they are right
     * @param choices: an array of indexes
     * @throws InvalidTileIndexInLittleHandException: thrown to avoid wrong order indexes
     */
    private void getTiles(int[] choices) throws InvalidTileIndexInLittleHandException {
        Scanner scanner = new Scanner(System.in);

        for(int i=0; i<choices.length; i++){
            choices[i] = scanner.nextInt();
            if(choices[i]<0 || choices[i]> choices.length) throw new InvalidTileIndexInLittleHandException(choices.length);
        }
        for(int i=0; i< choices.length-1; i++){
            for(int j=i+1; j<choices.length; j++){
                if(choices[i]==choices[j]) throw new InvalidTileIndexInLittleHandException(choices.length);
            }
        }
    }
}
