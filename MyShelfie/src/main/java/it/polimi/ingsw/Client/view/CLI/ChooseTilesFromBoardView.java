package it.polimi.ingsw.Client.view.CLI;

import it.polimi.ingsw.utils.Exceptions.InvalidRuleAnswerException;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.utils.Exceptions.InvalidDirectionException;
import it.polimi.ingsw.utils.Exceptions.InvalidNumberOfTilesException;
import it.polimi.ingsw.utils.Exceptions.OutOfBoardException;
import it.polimi.ingsw.utils.ReadFileByLines;
import it.polimi.ingsw.Server.Model.Tile;
import java.util.InputMismatchException;
import java.util.Scanner;
import static it.polimi.ingsw.utils.ColorCode.*;

/**
 * VIEW that prints the board, the common goal cards and the personal card of the player.
 * @author Irene Lo Presti
 */
public class ChooseTilesFromBoardView extends View {

    public int MAX_COLUMNS = 9;
    public int MAX_ROWS = 9;

    private final YourTurnMsg yourTurnMsg;
    private InitialPositionAnswer initialPositionAnswer;
    private PlayerChoiceAnswer playerChoiceAnswer;

    /**
     * Constructor method
     * @param yourTurnMsg: message from the server that contains the player's nickname, his/her personal card,
     *                 the board, the common cards, the number of turn and the order in which the players will play.
     */
    public ChooseTilesFromBoardView(YourTurnMsg yourTurnMsg){
        this.yourTurnMsg = yourTurnMsg;
    }

    @Override
    public void run() {

        int r, c, numberOfTiles;
        char direction, ruleAnswer;
        boolean goOn = false;
        Scanner scanner = new Scanner(System.in);



        //If it's the first turn, it prints the order in which the players will play
        if(yourTurnMsg.firstTurn){
            do{
                try {
                    ruleAnswer = getRuleAnswer();
                    break;
                } catch (InvalidRuleAnswerException e) {
                    System.out.println(e);
                }
            }while(true);
            if(ruleAnswer == 'Y')
                printRules();
            printOrderOfPlayers();
        }
        //printCommonGoalCardsInfo();
        printCommonGoalCardsInfoSide2Side();
        printPersonalGoalCardOnPlayerShelf();

        printBoard(-1, -1);

        System.out.println();
        System.out.println(yourTurnMsg.nickname + ", it's your turn to pick the tiles from the board!");
        System.out.println(yourTurnMsg.nickname + ", choose the position of the first tile, remember that\n" +
                " after you choose it, you have to select the number of tiles you want and\n" +
                " the direction (north, south, east, west) in which\n" +
                " you want to choose these other tiles\n");
        System.out.println("""
                Remember also that the board is 9x9 and that you have to choose a cell
                with a tile (so it has to be valid and not empty) with a free side
                """);

        //choosing initial row and initial column
        synchronized (this) {
            do {
                // client side exception management
                do {
                    try {
                        r = getInitialRow();
                        break;
                    } catch (OutOfBoardException e) {
                        System.out.println(e);
                    }
                } while (true);

                // client side exception management
                do {
                    try {
                        c = getInitialColumn();
                        break;
                    } catch (OutOfBoardException e) {
                        System.out.println(e);
                    }
                } while (true);

                r--;
                c--;


                InitialPositionMsg initialPositionMsg = new InitialPositionMsg(r, c);
                if (!getOwner().isRMI()) {
                    getOwner().getServerHandler().sendMessageToServer(initialPositionMsg);
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                else {
                    getOwner().getServerHandler().sendMessageToServer(initialPositionMsg);
                }

                System.out.println(this.initialPositionAnswer.answer);
                if (this.initialPositionAnswer.valid)
                    goOn = true;

            } while (!goOn);
        }

        if(yourTurnMsg.maxTilesPickable-1 != 0){
            printBoard(r, c);
            System.out.println("The initial position is marked with a star.\n");

            //choosing the number of tiles to pick and the direction
            synchronized (this) {
                goOn = false;
                do {
                    do {
                        try {
                            numberOfTiles = getNumberOfTiles();
                            break;
                        } catch (InvalidNumberOfTilesException e) {
                            System.out.println(e);
                        }
                    } while (true);

                    if (numberOfTiles == 0)
                        direction = '0';
                    else {
                        do {
                            try {
                                direction = getDirection();
                                break;
                            } catch (InvalidDirectionException e) {
                                System.out.println(e);
                            }
                        } while (true);
                    }

                    PlayerChoiceMsg playerChoiceMsg = new PlayerChoiceMsg(r, c, direction, numberOfTiles,
                            yourTurnMsg.maxTilesPickable);
                    if(!getOwner().isRMI()) {
                        getOwner().getServerHandler().sendMessageToServer(playerChoiceMsg);
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else{
                        getOwner().getServerHandler().sendMessageToServer(playerChoiceMsg);
                    }
                    System.out.println(this.playerChoiceAnswer.answer);
                    if (this.playerChoiceAnswer.valid)
                        goOn = true;

                } while (!goOn);
            }
        }
        else{
            PlayerChoiceMsg playerChoiceMsg = new PlayerChoiceMsg(r, c, '0', 0,
                    yourTurnMsg.maxTilesPickable);


            synchronized (this) {
                if (!getOwner().isRMI()) {
                    getOwner().getServerHandler().sendMessageToServer(playerChoiceMsg);
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    getOwner().getServerHandler().sendMessageToServer(playerChoiceMsg);
                }
            }

        }
    }

    //- - - - - - - - - - - - - - - - - - -| PRINT METHODS |- - - - - - - - - - - - - - - - - - -
    /**
     * This method prints all the rules, with an example
     */
    public void printRules(){
        System.out.println("RULES TO PLAY MY SHELFIE:");
        System.out.println("Goal of the game:");
        System.out.println("""
                Players take item tiles from the living room (the board) and place them in
                their bookshelves to score points; the game ends when a player
                completely fills their bookshelf. The player with more points at
                the end will win the game. There are 4 ways to score points:""");
        System.out.println("\n1) Personal goal card:");
        System.out.println("""
                \tThe personal goal card grants points if you match the highlighted spaces
                \twith the corresponding item tiles. You will see the personal goal directly in your
                \tpersonal shelf""");
        System.out.println("\n2) Common goal cards:");
        System.out.println("""
                \tThe common goal cards grant points to the players who achieve the illustrated
                \tpattern. Every common goal card has a detailed description, so don't worry!
                \tThe first player to achieve the personal goal wins 8 points, the second one 6 points
                \tthe third one 4 points and the last one 2 points. It isn't possible to achieve the
                \tsame goal multiple times.""");
        System.out.println("\n3) Adjacent Item tiles:");
        System.out.println("""
                \tGroups of adjacent item tiles of the same color on your bookshelf grant points
                \tdepending on how many tiles are connected (with one side touching).
                \tPoints:
                \t\t 3 tiles connected: 2 points
                \t\t 4 tiles connected: 3 points
                \t\t 5 tiles connected: 5 points
                \t\t6+ tiles connected: 8 points
                \tLet's make an example:""");

        Tile[][] example = new Tile[6][5];
        ReadFileByLines reader = new ReadFileByLines();
        //reader.readFrom("MyShelfie/src/txtfiles/ExampleForRules.txt");
        reader.readFrom("src/txtfiles/ExampleForRules.txt");
        for (int i = 0; i < 6; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                example[i][j] = Tile.valueOf(values[j]);
        }
        printSmallMatrix(example);

        System.out.println("""
                \t8 PINK tiles: 8 points
                \t4 LIGHT BLUE tiles: 3 points
                \t5 GREEN tiles: 5 points
                \t4 BLUE tiles: 3 points
                \t3 YELLOW tiles: 2 points
                \tTotal: 21 points""");
        System.out.println("\n4) Game-end trigger:");
        System.out.println("\tThe first player who completely fills their bookshelf scores 1 additional point.");

        System.out.println("\n\nGame play:");
        System.out.println("""
                The game is divided in turns that take place in a clockwise order starting from
                the first player. The first player is chosen randomly.
                During your turn, you must take 1, 2 o 3 item tiles from living room board, following\s
                these rules:
                """);
        System.out.println("\tThe tiles you take must be adjacent to each other and form a straight line.\n");
        System.out.println("""
                \tAll the tiles you take have at least one side free (not touching directly other
                \ttiles) at the beginning of your turn (i.e. you cannot take a tile that becomes free
                \tafter your first pick).
                """);
        System.out.print("""
                \tThen, you must place all the tiles you've picked into 1 column of your bookshelf.
                \tYou can decide the order, but you cannot place tiles in more than 1 column in a single turn.
                """);
        System.out.println("Note: You cannot take tiles if you don't have enough available space in your bookshelf\n");
        System.out.print("""
                At the end of a turn, if there are only item tiles without any other adjacent tile on
                the board, the board will automatically refill.

                """);
        System.out.println("Game end:");
        System.out.println("""
                \tThe first player who fills all the spaces of their bookshelf takes the end game
                \ttoken. The game continues until the last player(the one before the first)
                \thas played their turn.

                """);
        System.out.println("[press enter to continue]");
        Scanner scanner = new Scanner(System.in);
        String goOn = scanner.nextLine();

        if(goOn != null)
            return;
    }

    /**
     * This method prints the order of the players
     */
    public void printOrderOfPlayers(){
        if(yourTurnMsg.nickname.equals(yourTurnMsg.playersNames.get(0)))
            System.out.println(yourTurnMsg.nickname + ", you have the chair so you are the first one to play!");
        System.out.println("This is the order of playing:");
        for(int i=0; i<yourTurnMsg.playersNames.size(); i++)
            System.out.println((i+1)+") "+yourTurnMsg.playersNames.get(i));
        System.out.println();
    }

    /**
     * This method prints the common cards
     * @deprecated
     */
    public void printCommonGoalCardsInfo(){
        System.out.println("Common goal cards:");
        for(int i=0; i<yourTurnMsg.commonGoalCards.length; i++){
            printSmallMatrix(yourTurnMsg.commonGoalCards[i].getCardInfo().getSchema());
            System.out.println("x"+yourTurnMsg.commonGoalCards[i].getCardInfo().getTimes());
            System.out.println(yourTurnMsg.commonGoalCards[i].getCardInfo().getDescription());
            System.out.println();
        }
    }

    /**
     * This method allows to print the goal cards with the possibility to visualize the common cards side to side.
     * @author Andrea Giacalone
     */
    public void printCommonGoalCardsInfoSide2Side(){

        System.out.println("Common goal cards:");
        String code = "\u25CF";
        for(int r=0; r<6; r++) {
            for (int i = 0; i < yourTurnMsg.commonGoalCards.length; i++) {
                for (int c = 0; c < 5; c++) {

                    switch (yourTurnMsg.commonGoalCards[i].getCardInfo().getSchema()[r][c]) {
                        case NOT_VALID -> System.out.print(" ");
                        case BLANK -> System.out.print(BLANK.code + code + RESET.code);
                        case PINK -> System.out.print(PINK.code + code + RESET.code);
                        case GREEN -> System.out.print(GREEN.code + code + RESET.code);
                        case BLUE -> System.out.print(BLUE.code + code + RESET.code);
                        case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
                        case WHITE -> System.out.print(WHITE.code + code + RESET.code);
                        case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
                    }
                    System.out.print("\t");
                }
                System.out.print("\t");
                System.out.print("\t");
            }
            System.out.println();
        }

        for(int i=0; i<yourTurnMsg.commonGoalCards.length; i++){
            System.out.println("Common Goal Card #" +(i+1));
            System.out.println("x"+yourTurnMsg.commonGoalCards[i].getCardInfo().getTimes());
            System.out.println(yourTurnMsg.commonGoalCards[i].getCardInfo().getDescription());
            System.out.println();
        }

    }

    /**
     * This method prints the player's shelf with the instruction for the personal goal
     * @author Riccardo Lodelli
     */
    public void printPersonalGoalCardOnPlayerShelf() {
        Tile[][] myShelf = yourTurnMsg.shelfSnapshot;
        Tile[][] personalGoalCardShelf = yourTurnMsg.personalGoalCard.getPattern();

        System.out.println(yourTurnMsg.nickname + ", this is your shelf, empty circles represent where to place tiles to\n" +
                "achieve personal goal card:");

        // Printing column's indexes...
        System.out.print(/*"\u2716" +*/ "\t");
        for (int i = 0; i < myShelf[0].length; i++)
            System.out.print( (i+1) + "\t" );
        System.out.println();

        for (int i = 0; i < myShelf.length; i++) {
            // Printing row's indexes...
            System.out.print( /*(i+1) +*/ "\t" );
            // Printing the shelf...
            for (int j = 0; j < myShelf[0].length; j++) {
                // printing empty circles in personal goal card positions, filled circles in other cases
                if (personalGoalCardShelf[i][j] != Tile.BLANK && myShelf[i][j] == Tile.BLANK)
                    printTile(personalGoalCardShelf[i][j],"\u25cb");
                else
                    printTile(myShelf[i][j],"\u25CF");
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * This method prints the pattern of the cards
     * @param pattern: matrix of tiles with the pattern to follow in order to achieve the goal (personal or common)
     */
    public void printSmallMatrix(Tile[][] pattern){
        String code = "\u25CF";
        for(int r=0; r<6; r++){

            for(int c=0; c<5; c++){
                if(c==0)
                    System.out.print("\t");
                switch (pattern[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(BLANK.code + code + RESET.code);
                    case PINK -> System.out.print(PINK.code + code + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + code + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + code + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + code + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    /**
     * This method prints the correct color given the tile
     * @param tile to print
     * @param code: dot or *
     */
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
     * This method prints the board
     */
    public void printBoard(int initialRow, int initialColumn){

        String code;

        //printing the indexes of the columns
        System.out.print(/*"\u2716\t"*/ "\t");
        for(int i=0; i<MAX_COLUMNS; i++)
            System.out.print((i+1)+"\t");
        System.out.println();

        for(int r=0; r<MAX_ROWS; r++){
            //printing the indexes of the rows
            System.out.print((r+1)+"\t");

            //printing the tiles
            for(int c=0; c<MAX_COLUMNS; c++){

                if(r == initialRow && c == initialColumn)
                    code = "*";
                else
                    code = "\u25CF";

                switch (yourTurnMsg.boardSnapshot[r][c]) {
                    case NOT_VALID -> System.out.print(" ");
                    case BLANK -> System.out.print(BLANK.code + code + RESET.code);
                    case PINK -> System.out.print(PINK.code + code + RESET.code);
                    case GREEN -> System.out.print(GREEN.code + code + RESET.code);
                    case BLUE -> System.out.print(BLUE.code + code + RESET.code);
                    case LIGHTBLUE -> System.out.print(LIGHTBLUE.code + code + RESET.code);
                    case WHITE -> System.out.print(WHITE.code + code + RESET.code);
                    case YELLOW -> System.out.print(YELLOW.code + code + RESET.code);
                }
                System.out.print("\t");
            }
            System.out.print(r+1);
            System.out.println();
        }
        System.out.print(" \t");
        for(int r=0; r<MAX_ROWS; r++)
            //printing the indexes of the rows
            System.out.print((r+1)+"\t");
        System.out.println();
    }

    //- - - - - - - - - - - - - - - - -| INPUT METHODS |- - - - - - - - - - - - -  - - - -
    /**
     * This method gets if the player wants to read the rules
     * @return 'Y' (yes) or 'N' (no)
     * @throws InvalidRuleAnswerException if the answer is not 'y', 'n', 'yes', 'no'
     */
    private char getRuleAnswer() throws InvalidRuleAnswerException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to read the rules? (y-n) ");
        String input = scanner.next().toUpperCase();
        if(input.length() > 1 && !input.equals("NO") && !input.equals("YES")) throw new InvalidRuleAnswerException();

        char answer = input.charAt(0);
        if(answer!='Y' && answer!='N') throw new InvalidRuleAnswerException();
        else return answer;
    }

    /**
     * Getter method for the initial row
     * @return row>=1 && row<MAX_ROWS
     * @throws OutOfBoardException if the row is not between 1 and MAX_ROWS
     */
    private int getInitialRow() throws OutOfBoardException {
        int r;
        System.out.print("Please insert the row of the initial position: ");
        Scanner scanner = new Scanner(System.in);
        do{
            try{
                r = scanner.nextInt();
                break;
            }catch(InputMismatchException e){
                System.out.println("You have to insert a number. Try again!");
                scanner.next();
            }
        }while(true);
        if(r<=0 || r>MAX_ROWS) throw new OutOfBoardException();
        return r;
    }

    /**
     * Getter method for the initial column
     * @return column>=1 && column<MAX_ROW
     * @throws OutOfBoardException if the column is not between 1 and MAX_COLUMNS
     */
    private int getInitialColumn() throws OutOfBoardException{
        int c;
        System.out.print("Please insert the column of the initial position: ");
        Scanner scanner = new Scanner(System.in);
        do{
            try{
                c = scanner.nextInt();
                break;
            }catch(InputMismatchException e){
                System.out.println("You have to insert a number. Try again!");
                scanner.next();
            }
        }while(true);
        if(c<=0 || c>MAX_COLUMNS) throw new OutOfBoardException();
        else return c;
    }

    /**
     * Getter method for the number of tiles
     * @return number of tiles >= 0 && number of tiles < maxTilesPickable-1
     * @throws InvalidNumberOfTilesException if the number of tiles is not between 0 and axTilesPickable-1
     */
    private int getNumberOfTiles() throws InvalidNumberOfTilesException{

        System.out.println(yourTurnMsg.nickname + ", now it's time to" +
                " insert the number of tiles that you\n" +
                "want to chose (other than the one that you have already chose).");

        System.out.println("Remember: you can chose between 0 (if you don't want to pick others tiles)\n" +
                "and "+(yourTurnMsg.maxTilesPickable-1)+".");
        System.out.print("Please insert the number of tiles: ");

        Scanner scanner = new Scanner(System.in);
        int numberOfTiles;
        do{
            try{
                numberOfTiles = scanner.nextInt();
                break;
            }catch(InputMismatchException e){
                System.out.println("You have to insert a number. Try again!");
                scanner.next();
            }
        }while(true);

        if(numberOfTiles<0 || numberOfTiles>yourTurnMsg.maxTilesPickable-1)
            throw new InvalidNumberOfTilesException(yourTurnMsg.maxTilesPickable);

        else
            return numberOfTiles;
    }

    /**
     * Getter method for direction
     * @return direction (n, s, w or e)
     * @throws InvalidDirectionException if the direction is not n, s, w or e
     */
    private char getDirection() throws InvalidDirectionException {

        System.out.println(yourTurnMsg.nickname + ", now it's time to" +
                " insert the direction in which you want to choose those tiles.");
        System.out.println("Remember: you can choose between n (north), s (south), e (east), w (west).");
        System.out.print("Please insert the direction: ");

        Scanner scanner = new Scanner(System.in);
        String answer;
        answer = scanner.next().toLowerCase();
        if (answer.length() > 1) throw new InvalidDirectionException();

        char direction;
        direction = answer.charAt(0);

        if (direction != 'n' && direction != 's' && direction != 'e' && direction != 'w')
            throw new InvalidDirectionException();
        else return direction;
    }

    //- - - - - - - - - - - - - - - - - - - -| SETTER METHODS |- - - - - - - - - - - - - - - - - - - - - -
    public void setInitialPositionAnswer(InitialPositionAnswer answer){
        this.initialPositionAnswer = answer;
    }
    public void setPlayerChoiceAnswer(PlayerChoiceAnswer answer){
        this.playerChoiceAnswer = answer;
    }

    public void notifyView(){
        synchronized (this) {
            this.notify();
        }
    }
}
