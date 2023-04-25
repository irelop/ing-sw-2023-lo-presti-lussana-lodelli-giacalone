package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Server.Messages.MyShelfMsg;
import it.polimi.ingsw.Server.Model.ReadFileByLines;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class InsertInShelfViewTest {
    Tile[][] shelf;
    ArrayList<Tile> hand;
    InsertInShelfView view;

    MyShelfMsg msg;


    @BeforeEach
    void setUp() {
        hand = new ArrayList<>();
        hand.add(Tile.LIGHTBLUE);
        hand.add(Tile.GREEN);

        shelf = new Tile[6][5];
        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/shelf_view.txt");

        for (int i = 0; i < 6; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                shelf[i][j] = Tile.valueOf(values[j]);
        }

        msg = new MyShelfMsg(this.shelf,hand);

        view = new InsertInShelfView(msg);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void printShelf_visualTest(){
        view.printShelf(shelf);
    }
}
