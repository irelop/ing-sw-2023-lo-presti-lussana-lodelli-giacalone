package it.polimi.ingsw.PatternStrategy;

import it.polimi.ingsw.PersonalGoalCard;
import it.polimi.ingsw.ReadFileByLines;
import it.polimi.ingsw.Tile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLOutput;

import static org.junit.Assert.*;

public class PersonalGoalCardTest {

    PersonalGoalCard myCard;
    Tile[][] playerShelf;

    @Before
    public void setUp() {

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/PersonalGoalCardTest.txt");

        myCard = new PersonalGoalCard();
        playerShelf = new Tile[6][5];
        for (int i = 0; i < 6; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 5; j++)
                playerShelf[i][j] = Tile.valueOf(values[j]);
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getPersonalGoalScore_test1() {
        int tot = 0;
        for (int i = 0; i < 6; i++) {
            tot += myCard.getPersonalGoalScore(playerShelf);
        }
        assertEquals(9,tot);
    }

}