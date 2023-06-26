package it.polimi.ingsw.Server.Model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PersonalGoalDeck class
 * @author Andrea Giacalone
 */
public class CommonGoalDeckTest {
    CommonGoalDeck testDeck;
    @Before
    public void setUp() {
        testDeck = new CommonGoalDeck();

    }

    @After
    public void tearDown() {
        testDeck = null;
    }

    /**
     * Test for drawCommon() method: ensures that the drawn card
     * is not null and the deck has other 11 cards left
     */
    @Test
    public void singleDrawCommon_shouldReturnSingleCommonCard() {
        CommonGoalCard drawnCommon = testDeck.drawCommon();
        assertNotEquals(null,drawnCommon);
        assertEquals(11,testDeck.getRemainingCommonGoalCards());
    }

    /**
     * Test for drawCommon() method: drawing every card ensures that its impossible
     * to draw 2 times the same card and that the deck removes cards once per time
     */
    @Test
    public void allPossibleDraw_shouldReturnAllDistinctStrategies(){
        String[] allDistinctStrategies = new String[12];
        for(int i = 0; i< 12;i++){
            allDistinctStrategies[i] = testDeck.drawCommon().getStrategyName();
        }
        assertEquals(12, Arrays.stream(allDistinctStrategies).filter(x -> x != null).distinct().count());
    }

    /**
     * Test for drawCommon() method: ensures that drawing the 13th card
     * from the deck it throws an IllegalArgumentException
     */
    @Test
    public void unallowedDraw_shouldThrowIllegalArgumentException(){
        CommonGoalCard drawnCommon;
        for(int i = 0;i<12;i++){
            drawnCommon = testDeck.drawCommon();
        }

        assertThrows(IllegalArgumentException.class,()->testDeck.drawCommon());
    }
}