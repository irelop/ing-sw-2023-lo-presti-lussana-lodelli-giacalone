package it.polimi.ingsw.Server.Model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PersonalGoalDeck class
 * @author Riccardo Lodelli
 */
public class PersonalGoalDeckTest {

    PersonalGoalDeck myDeck;

    @Before
    public void setUp() {
        myDeck = new PersonalGoalDeck();
    }

    @After
    public void tearDown() {    }

    /**
     * Test for drawPersonal() method: ensures that the drawn card
     * is not null and the deck has other 11 cards left
     */
    @Test
    public void draw_Once() {
        PersonalGoalCard drawnCommon = myDeck.drawPersonal();
        assertNotEquals(null,drawnCommon);
        assertEquals(11,myDeck.getCurrentSize());
    }

    /**
     * Test for drawPersonal() method: drawing every card ensures that its impossible
     * to draw 2 times the same card and that the deck removes cards once per time
     */
    @Test
    public void draw_All() {
        /*
         * Checking if every card I draw is different from the others remaining in deck
         */
        PersonalGoalCard drawn;
        int currSize;
        boolean flag = true;
        for (int i = 0; i < 12; i++) {
            drawn = myDeck.drawPersonal();
            currSize = myDeck.getCurrentSize();
            assertEquals(11-i,currSize);
            for (int j = 0; j < currSize; j++) {
                for (int k = 0; k < 6; k++) {
                    for (int l = 0; l < 5; l++) {
                        if (myDeck.getPersonalGoalCardDeck().get(j).getPattern()[k][l] != drawn.getPattern()[k][l]) {
                            flag = false;
                            break;
                        }
                    }
                }
                assertFalse(flag);
            }
        }
    }

}
