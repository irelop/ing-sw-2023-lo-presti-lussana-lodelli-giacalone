package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Model.*;
import it.polimi.ingsw.Server.Messages.MyShelfMsg;
import it.polimi.ingsw.Server.Model.PatternStrategy.V2UPatternStrategy;
import it.polimi.ingsw.Server.Model.ReadFileByLines;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class InsertInShelfViewTest {
    Tile[][] shelf;
    ArrayList<Tile> hand;

    CommonGoalCard[] commonGoalCards;

    PersonalGoalCard personalGoalCard;
    InsertInShelfView view;

    MyShelfMsg msg;


    @BeforeEach
    void setUp() {

        // -- Declarations and Initializations --

        // file reader
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/shelf_view.txt");

        // little hand
        hand = new ArrayList<>();
        hand.add(Tile.LIGHTBLUE);
        hand.add(Tile.GREEN);

        // shelf
        shelf = new Tile[6][5];
        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();
            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");
            for (int j = 0; j < 5; j++)
                shelf[i][j] = Tile.valueOf(values[j]);
        }

        // personal goal card
        Tile[][] pgcShelf = new Tile[6][5];
        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();
            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");
            for (int j = 0; j < 5; j++)
                pgcShelf[i][j] = Tile.valueOf(values[j]);
        }
        personalGoalCard = new PersonalGoalCard(pgcShelf);

        // commond goal cards
        commonGoalCards = new CommonGoalCard[2];
        String name, description;
        int times;
            // first
        Tile[][] cgc1Shelf = new Tile[6][5];
        name = ReadFileByLines.getLine();
        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();
            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");
            for (int j = 0; j < 5; j++)
                cgc1Shelf[i][j] = Tile.valueOf(values[j]);
        }
        times = Integer.parseInt(ReadFileByLines.getLine());
        description = ReadFileByLines.getLine();
        commonGoalCards[0] = new CommonGoalCard(new V2UPatternStrategy(),name,cgc1Shelf,times,description);

            // second
        Tile[][] cgc2Shelf = new Tile[6][5];
        name = ReadFileByLines.getLine();
        for (int i = 0; i < 6; i++) {
            String row = ReadFileByLines.getLine();
            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");
            for (int j = 0; j < 5; j++)
                cgc2Shelf[i][j] = Tile.valueOf(values[j]);
        }
        times = Integer.parseInt(ReadFileByLines.getLine());
        description = ReadFileByLines.getLine();
        commonGoalCards[1] = new CommonGoalCard(new V2UPatternStrategy(),name,cgc2Shelf,times,description);

        // myShelfMsg and view
        msg = new MyShelfMsg(this.shelf,hand,commonGoalCards,personalGoalCard);
        view = new InsertInShelfView(msg);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void printShelf_visualTest(){
        view.printShelf(shelf);
    }

    @Test
    public void cards_visualTest(){
        view.printGoalCardsInfo();
    }
}
