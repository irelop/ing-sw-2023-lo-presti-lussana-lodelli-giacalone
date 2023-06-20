package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.YourTurnMsg;
import it.polimi.ingsw.Server.Model.*;
import it.polimi.ingsw.Server.Model.PatternStrategy.Q2UPatternStrategy;
import it.polimi.ingsw.Server.Model.PatternStrategy.V6DPatternStrategy;
import it.polimi.ingsw.utils.ReadFileByLines;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ChooseTilesFromBoardViewTest {


    private ChooseTilesFromBoardView chooseTilesFromBoardView;
    private ReadFileByLines reader;
    private YourTurnMsg yourTurnMsg;

    @BeforeEach
    void setUp() {

        reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/chooseTilesFromBoardViewTest.txt");

        //boardGrid
        Tile[][] boardGrid = new Tile[9][9];
        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                boardGrid[i][j] = Tile.valueOf(values[j]);
        }

        //common cards
        CommonGoalCard[] commonGoalCards = new CommonGoalCard[2];

        String commonCardName = ReadFileByLines.getLine();
        Tile[][] commonCardPattern1 = new Tile[6][5];
        for(int i=0; i<6; i++){
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                commonCardPattern1[i][j] = Tile.valueOf(values[j]);
        }
        int commonCardTimes = Integer.parseInt(ReadFileByLines.getLine());
        String commonCardDescription = ReadFileByLines.getLine();
        commonGoalCards[0] = new CommonGoalCard(new Q2UPatternStrategy(), commonCardName, commonCardPattern1, commonCardTimes, commonCardDescription);

        commonCardName = ReadFileByLines.getLine();

        Tile[][] commonCardPattern2 = new Tile[6][5];

        for(int i=0; i<6; i++){
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                commonCardPattern2[i][j] = Tile.valueOf(values[j]);
        }
        commonCardTimes = Integer.parseInt(ReadFileByLines.getLine());
        commonCardDescription = ReadFileByLines.getLine();
        commonGoalCards[1] = new CommonGoalCard(new V6DPatternStrategy(), commonCardName, commonCardPattern2, commonCardTimes, commonCardDescription);

        //personal card
        Tile[][] personalCardPattern = new Tile[6][5];
        for(int i=0; i<6; i++){
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                personalCardPattern[i][j] = Tile.valueOf(values[j]);
        }
        PersonalGoalCard personalGoalCard = new PersonalGoalCard(personalCardPattern);

        Tile[][] shelf = new Tile[6][5];
        for(int i=0; i<6; i++){
            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                shelf[i][j] = Tile.valueOf(values[j]);
        }

        ArrayList<String> playersNames = new ArrayList<>();
        playersNames.add("player1");
        playersNames.add("player2");

        yourTurnMsg = new YourTurnMsg("player1", 3, boardGrid, commonGoalCards,
                personalGoalCard, true, playersNames, shelf,false);
        chooseTilesFromBoardView = new ChooseTilesFromBoardView(yourTurnMsg);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void printInitialBoard_visualTest(){
        chooseTilesFromBoardView.printBoard(-1,-1);
    }

    @Test
    public void printBoardWithStar_visualTest(){
        chooseTilesFromBoardView.printBoard(2,4);
    }

    @Test
    public void printCards_visualTest(){
        chooseTilesFromBoardView.printCommonGoalCardsInfoSide2Side();
    }

    @Test
    public void printPlayersOrder_visualTest(){
        chooseTilesFromBoardView.printOrderOfPlayers();
    }

    @Test
    public void printRules_visualTest(){
        chooseTilesFromBoardView.printRules();
    }

}