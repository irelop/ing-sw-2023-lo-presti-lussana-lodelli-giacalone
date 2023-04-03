package it.polimi.ingsw;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void singleDrawCommon_shouldReturnSingleCommonCard() {
        CommonGoalCard drawnCommon = testDeck.drawCommon();
        assertNotEquals(null,drawnCommon);
        assertEquals(11,testDeck.getRemainingCommonGoalCards());
    }

    @Test
    public void allPossibleDraw_shouldReturnAllDistinctStrategies(){
        String[] allDistinctStrategies = new String[12];
        for(int i = 0; i< 12;i++){
            allDistinctStrategies[i] = testDeck.drawCommon().getStrategyName();
        }
        assertEquals(12, Arrays.stream(allDistinctStrategies).filter(x -> x != null).distinct().count());
    }

    @Test
    public void unallowedDraw_shouldThrowIllegalArgumentException(){
        CommonGoalCard drawnCommon;
        for(int i = 0;i<12;i++){
            drawnCommon = testDeck.drawCommon();
        }

        assertThrows(IllegalArgumentException.class,()->testDeck.drawCommon());
    }
}