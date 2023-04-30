package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Model.Exceptions.InvalidShelfColumnException;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.Server.Model.Tile;
import it.polimi.ingsw.Server.Messages.InsertingTilesMsg;
import it.polimi.ingsw.Server.Messages.MyShelfMsg;
import static it.polimi.ingsw.Client.View.ColorCode.*;

import java.util.ArrayList;
import java.util.Scanner;

// prima bozza
// DEVO MOSTRARE LE CARTE OBIETTIVO COMUNE E PERSONALE
// Manca la gestione delle eccezioni lato server

/**
 * This view is shown when the current player has to insert the tiles he/she took from the board into
 * his/her personal shelf. The player interacts with this view to choose the column he/she wants to fill
 * with tiles and to put in order the tiles that must be placed in the shelf
 *
 * @author Riccardo Lodelli
 */

public class InsertInShelfView extends View {

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
                    case BLANK -> System.out.print(BLANK.code + circle + RESET.code);
                    case PINK -> System.out.print(PINK.code + circle + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + circle + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + circle + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + circle + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + circle + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + circle + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * The main method of the view, overrided form thread run() method
     * It sends an InsertingTilesMsg containing player's choises (if right)
     */
    @Override
    public void run() {

        int columnChosen;
        int[] chosenOrderIndexes;
        ArrayList<Tile> chosenTiles = msg.getLittleHand();
        Tile[][] myShelf = msg.getShelf();
        boolean goOn = false;

        synchronized (this) {

            printShelf(myShelf);
            do {
                do {
                    // client side exception management
                    try {
                        columnChosen = chooseColumn();
                        break;
                    } catch (InvalidShelfColumnException e) {
                        System.out.println(e);
                    }
                } while (true);

                do {
                    // client side exception management
                    try {
                        chosenOrderIndexes = askOrder(chosenTiles);
                        break;
                    } catch (InvalidTileIndexInLittleHandException e) {
                        System.out.println(e);
                    }
                } while (true);

                InsertingTilesMsg insertingMsg = new InsertingTilesMsg(columnChosen, chosenOrderIndexes);
                getOwner().getServerHandler().sendMessageToServer(insertingMsg);
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(insertingMsg.answer.answer);
                if (insertingMsg.answer.valid)
                    goOn = true;
            } while (!goOn);
        }
    }

    /**
     * This method allows the player to choose the column where he/she wants to place tiles
     * @return the index of the column chosen by the player
     * @throws InvalidShelfColumnException: thrown to avoid wrong indexes for the column
     */
    public int chooseColumn() throws InvalidShelfColumnException {
        int columnChosen;
        System.out.println("choose the Column:");
        Scanner scanner = new Scanner(System.in);
        columnChosen = scanner.nextInt() - 1; // user's indexes start from one
        if (columnChosen < 0 || columnChosen >= 5) throw new InvalidShelfColumnException();
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
            choices[0] = 0;
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
                choices[i] = i;
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
            choices[i] = scanner.nextInt() - 1; // user's indexes start from one
            if(choices[i] < 0 || choices[i] >= choices.length) throw new InvalidTileIndexInLittleHandException(choices.length);
        }
        for(int i=0; i< choices.length-1; i++){
            for(int j=i+1; j<choices.length; j++){
                if(choices[i]==choices[j]) throw new InvalidTileIndexInLittleHandException(choices.length);
            }
        }
    }


    public void notifyView(){
        synchronized (this) {
            this.notify();
        }
    }
}
