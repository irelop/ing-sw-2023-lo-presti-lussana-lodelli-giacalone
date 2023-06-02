package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.InsertingTilesAnswer;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidShelfColumnException;
import it.polimi.ingsw.Server.Model.Exceptions.InvalidTileIndexInLittleHandException;
import it.polimi.ingsw.Server.Model.Tile;
import it.polimi.ingsw.Server.Messages.InsertingTilesMsg;
import it.polimi.ingsw.Server.Messages.ToShelfMsg;
import static it.polimi.ingsw.Client.View.ColorCode.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * This view is shown when the current player has to insert the tiles he/she took from the board into
 * his/her personal shelf. The player interacts with this view to choose the column he/she wants to fill
 * with tiles and to put in order the tiles that must be placed in the shelf
 *
 * @author Riccardo Lodelli
 */

public class InsertInShelfView extends View {

    private final ToShelfMsg msg;

    private InsertingTilesAnswer insertingTilesAnswer;

    public InsertInShelfView(ToShelfMsg msg){
        this.msg = msg;
    }

    /**
     * The main method of the view, overrided form thread run() method
     * It sends an InsertingTilesMsg to the server in order to validate user's inputs,
     * modify the model and switch to the next view
     */
    @Override
    public void run() {

        int columnChosen;
        int[] chosenOrderIndexes;
        ArrayList<Tile> chosenTiles = msg.getLittleHand();
        Tile[][] myShelf = msg.getShelf();
        boolean goOn = false;

        System.out.println(msg.commonGoalCards[0]);
        System.out.println(msg.commonGoalCards[1]);

        synchronized (this) {

            printCommonGoalCardsInfo();
            System.out.println("This is your shelf, empty circles represent where to place tiles to achieve personal goal card:");
            printShelf(myShelf);

            System.out.println("You picked these tiles:");
            for(int i=0; i<chosenTiles.size(); i++) {
                System.out.print((i + 1) + ") " + chosenTiles.get(i) + " ");
                printTile(chosenTiles.get(i),"\u25CF");
                System.out.println();
            }
            do {

                // client side exception management
                do {
                    try {
                        columnChosen = chooseColumn();
                        break;
                    } catch (InvalidShelfColumnException e) {
                        System.out.println(e);
                    }
                } while (true);

                // client side exception management
                do {
                    try {
                        chosenOrderIndexes = askOrder(chosenTiles);
                        break;
                    } catch (InvalidTileIndexInLittleHandException e) {
                        System.out.println(e);
                    }
                } while (true);

                // generating message and waiting for an answer
                InsertingTilesMsg insertingMsg = new InsertingTilesMsg(columnChosen, chosenOrderIndexes);
                if(!getOwner().isRMI()) {
                    getOwner().getServerHandler().sendMessageToServer(insertingMsg);
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                else{
                    try {
                        getOwner().getRemoteServer().sendMessageToServer(insertingMsg, getOwner().getClient());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(this.insertingTilesAnswer.answer);
                if (this.insertingTilesAnswer.valid)
                    goOn = true;

            } while (!goOn);
        }
    }

    public void notifyView(){
        synchronized (this) {
            this.notify();
        }
    }

    public void setInsertingTilesAnswer(InsertingTilesAnswer insertingTilesAnswer) {
        this.insertingTilesAnswer = insertingTilesAnswer;
    }

    /**
     * This method prints the player shelf to help him to decide what to do
     * @param myShelf: the shelf which has to be printed
     */
    public void printShelf(Tile[][] myShelf) {

        Tile[][] personalGoalCardShelf = msg.personalGoalCard.getPattern();

        // check indicates if I'm printing player's shelf
        boolean check = (myShelf == msg.getShelf());

        // Printing column's indexes...
        System.out.print("\u2716" + "\t");
        for (int i = 0; i < myShelf[0].length; i++)
            System.out.print( (i+1) + "\t" );
        System.out.println();

        for (int i = 0; i < myShelf.length; i++) {
            // Printing row's indexes...
            System.out.print( (i+1) + "\t" );
            // Printing the shelf...
            for (int j = 0; j < myShelf[0].length; j++) {
                // printing empty circles in personal goal card positions, filled circles in other cases
                if (check && personalGoalCardShelf[i][j] != Tile.BLANK && myShelf[i][j] == Tile.BLANK)
                    printTile(personalGoalCardShelf[i][j],"\u25cb");
                else
                    printTile(myShelf[i][j],"\u25CF");
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    public void printTile(Tile tile, String code) {
        switch (tile) {
            case NOT_VALID -> System.out.print(" ");
            case BLANK -> System.out.print(BLANK.code + code + RESET.code);
            case PINK -> System.out.print(PINK.code + code + RESET.code);
            case GREEN -> System.out.print(GREEN.code + code + RESET.code);
            case BLUE -> System.out.print(BLUE.code + code + RESET.code);
            case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
            case WHITE -> System.out.print(WHITE.code + code + RESET.code);
            case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
        }
    }

    /**
     * This method prints both common goal cards and player's personal
     * goal card in order to help him to decide the best move to achieve points
     */
    public void printCommonGoalCardsInfo(){
        System.out.println();
        System.out.println("Common goal cards:");
        for(int i=0; i<msg.commonGoalCards.length; i++){
            printShelf(msg.commonGoalCards[i].getCardInfo().getSchema());
            System.out.println("x"+msg.commonGoalCards[i].getCardInfo().getTimes()+" times");
            System.out.println(msg.commonGoalCards[i].getCardInfo().getDescription());
        }
        System.out.println();
    }

    /**
     * This method allows the player to choose the column where he/she wants to place tiles
     * @return the index of the column chosen by the player
     * @throws InvalidShelfColumnException: thrown to avoid wrong indexes for the column
     */
    public int chooseColumn() throws InvalidShelfColumnException {
        int columnChosen;
        System.out.print("Please insert the index of the column you want to fill with tiles: ");
        Scanner scanner = new Scanner(System.in);

        //to avoid loop if the user inserts a letter
        do{
            try{
                columnChosen = scanner.nextInt() -1 ;// user's indexes start from one
                break;
            }catch(InputMismatchException e){
                System.out.println("You have to insert a number. Try again!");
                scanner.next();
            }
        }while(true);
        if (columnChosen < 0 || columnChosen >= 5) throw new InvalidShelfColumnException();
        return columnChosen;
    }

    /**
     * This method allows the player to choose the order for the tiles he/she wants to place
     * @param chosenTiles: an array containing the tiles that the player has chosen from the board
     * @return an array of indexes which indicates the order wanted by the player
     * @throws InvalidTileIndexInLittleHandException: thrown to avoid wrong order indexes
     */
    private int[] askOrder(ArrayList<Tile> chosenTiles) throws InvalidTileIndexInLittleHandException {

        int tilesNumber = chosenTiles.size();
        int[] choices = new int[tilesNumber];
        boolean sameColors = true;

        // 1 tile to insert -> user doesn't have to choose the order
        if(chosenTiles.size()==1){
            choices[0] = 0;
            return choices;
        }

        // 2 or 3 tiles of the same color to insert -> user doesn't have to choose the order
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

        System.out.print("""
                You can reorder tiles before inserting them in your shelf:\s
                (remember: first index will be the lowest in the shelf)
                """);
        //getTiles(choices);

        for(int i=0; i<choices.length; i++){
            switch (i) {
                case 0 -> System.out.print("Please insert first index: ");
                case 1 -> System.out.print("Please insert second index: ");
                case 2 -> System.out.print("Please insert third index: ");
            }
            choices[i] = getTile();
        }
        for(int i=0; i< choices.length-1; i++){
            for(int j=i+1; j<choices.length; j++){
                if(choices[i]==choices[j]) throw new InvalidTileIndexInLittleHandException(choices.length);
            }
        }
        return choices;
    }

    /**
     * This private method is called by askOrder(), more than once
     * It takes player's input and check if it's right
     * @return choice: index chosen by the player
     * @throws InvalidTileIndexInLittleHandException: thrown to avoid wrong order indexes
     */
    private int getTile() throws InvalidTileIndexInLittleHandException {
        Scanner scanner = new Scanner(System.in);
        int size = msg.getLittleHand().size();
        int choice = scanner.nextInt() - 1; // user's indexes start from one
        if(choice < 0 || choice >= size) throw new InvalidTileIndexInLittleHandException(size);
        return choice;
    }

    /**
     * This private method is called by askOrder(). It takes player's inputs and check if they are right
     * @deprecated
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
        // better insert them one per time
    }

}
