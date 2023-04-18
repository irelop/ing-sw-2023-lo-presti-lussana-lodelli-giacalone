package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.PatternStrategy.*;
import it.polimi.ingsw.PatternStrategy.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * CommonGoalDeck class: this class represent the set of the playable common goal cards of the game, providing the
 * necessary functions for a random draw.
 *
 * @author Andrea Giacalone
 */
public class CommonGoalDeck {
    private static ArrayList<CommonGoalCard> commonGoalCardDeck;
    //private final static int MAX_SIZE = 12;


    public CommonGoalDeck() {
        commonGoalCardDeck = new ArrayList<>();
        commonGoalCardDeck.add(new CommonGoalCard(new M8PatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new APatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new XPatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard((new V4UPatternStrategy())));
        commonGoalCardDeck.add(new CommonGoalCard(new V2UPatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new V6DPatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new V6UPatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new PPatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new O5DPatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new O5UPatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new Q2UPatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new DPatternStrategy()));


    }

    /**
     * OVERVIEW: this method allows the draw of a common goal card, as specified by the game rules.
     * @return the drawn common goal card.
     */
    public static CommonGoalCard drawCommon() throws IllegalArgumentException{
        Random random = new Random();
        int idx = random.nextInt(commonGoalCardDeck.size());

            return commonGoalCardDeck.remove(idx);
    }

    public static int getRemainingCommonGoalCards(){
        return (commonGoalCardDeck.size());
    }

}
