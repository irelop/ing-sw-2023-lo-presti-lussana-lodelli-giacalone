package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Random;

/**
 * Draft of class CommonGoalCardDeck: this version wraps the base structure and a method that allows to randomly
 * draw a common goal card among the possible ones.
 */
public class CommonGoalCardDeck {
    private static ArrayList<CommonGoalCard> commonGoalCardDeck;
    private final static int MAX_SIZE = 2;


    public CommonGoalCardDeck(ArrayList<CommonGoalCard> commonGoalCardDeck) {
        commonGoalCardDeck = commonGoalCardDeck;
    }
    //method needed to be fixed: facciamo il singolo pick o direttamente i due pick delle carte qui?
    public static CommonGoalCard drawCommon() {
        Random random = new Random();
        int idx = random.nextInt(commonGoalCardDeck.size());

        return commonGoalCardDeck.get(idx);
    }

}
