package it.polimi.ingsw;

import it.polimi.ingsw.Server.Model.PersonalGoalCard;
import it.polimi.ingsw.Server.Model.ReadFileByLines;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

public class PersonalGoalCardTest {

    PersonalGoalCard myCard;
    Tile[][] playerShelf;
    @BeforeEach
    public void setUp() {

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/PersonalGoalCardTest.txt");

        myCard = new PersonalGoalCard();
        playerShelf = new Tile[6][5];

        String id = ReadFileByLines.getLine();

        for (int i = 0; i < 6; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                playerShelf[i][j] = Tile.valueOf(values[j]);
        }

    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void getPersonalGoalScore_test0() {
        int tot = 0;
        for (int i = 0; i < 6; i++) {
            tot += myCard.getPersonalGoalScore(playerShelf);
        }
        assertEquals(0,tot);
    }

    @Test
    public void getPersonalGoalScore_test1() {
        playerShelf[0][0] = Tile.GREEN;
        int tot = 0;
        //for (int i = 0; i < 6; i++) {
            tot += myCard.getPersonalGoalScore(playerShelf);
        //}
        assertEquals(1,tot);
    }
    @Test
    public void getPersonalGoalScore_test2() {
        playerShelf[0][0] = Tile.GREEN;
        playerShelf[1][3] = Tile.BLUE;
        int tot = 0;
        for (int i = 0; i < 6; i++) {
            tot += myCard.getPersonalGoalScore(playerShelf);
        }
        assertEquals(2,tot);
    }
    @Test
    public void getPersonalGoalScore_test3() {
        playerShelf[0][0] = Tile.GREEN;
        playerShelf[1][3] = Tile.BLUE;
        playerShelf[2][1] = Tile.PINK;
        int tot = 0;
        for (int i = 0; i < 6; i++) {
            tot += myCard.getPersonalGoalScore(playerShelf);
        }
        assertEquals(4,tot);
    }
    @Test
    public void getPersonalGoalScore_test4() {
        playerShelf[0][0] = Tile.GREEN;
        playerShelf[1][3] = Tile.BLUE;
        playerShelf[2][1] = Tile.PINK;
        playerShelf[3][0] = Tile.LIGHTBLUE;
        int tot = 0;
        for (int i = 0; i < 6; i++) {
            tot += myCard.getPersonalGoalScore(playerShelf);
        }
        assertEquals(6,tot);
    }
    @Test
    public void getPersonalGoalScore_test5() {
        playerShelf[0][0] = Tile.GREEN;
        playerShelf[1][3] = Tile.BLUE;
        playerShelf[2][1] = Tile.PINK;
        playerShelf[3][0] = Tile.LIGHTBLUE;
        playerShelf[4][4] = Tile.YELLOW;
        int tot = 0;
        for (int i = 0; i < 6; i++) {
            tot += myCard.getPersonalGoalScore(playerShelf);
        }
        assertEquals(9,tot);
    }
    @Test
    public void getPersonalGoalScore_test6() {
        playerShelf[0][0] = Tile.GREEN;
        playerShelf[1][3] = Tile.BLUE;
        playerShelf[2][1] = Tile.PINK;
        playerShelf[3][0] = Tile.LIGHTBLUE;
        playerShelf[4][4] = Tile.YELLOW;
        playerShelf[5][2] = Tile.WHITE;
        int tot = 0;
        for (int i = 0; i < 6; i++) {
            tot += myCard.getPersonalGoalScore(playerShelf);
        }
        assertEquals(12,tot);
    }
    @Test
    public void PersonalGoalCard_visualTest() {
        System.out.println(myCard.getId());
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(myCard.getPattern()[i][j].name() + "\t");
            }
            System.out.println();
        }
    }
}