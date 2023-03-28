package it.polimi.ingsw;

import it.polimi.ingsw.PatternStrategy.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Draft of class CommonGoalCardDeck: this version wraps the base structure and a method that allows to randomly
 * draw a common goal card among the possible ones.
 */
public class CommonGoalDeck {
    private static ArrayList<CommonGoalCard> commonGoalCardDeck;
    private final static int MAX_SIZE = 12;


    public CommonGoalDeck() {
        commonGoalCardDeck = new ArrayList<>();
        commonGoalCardDeck.add(new CommonGoalCard(new M8PatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new APatternStrategy()));
        commonGoalCardDeck.add(new CommonGoalCard(new XPatternStrategy()));
        //lo andiamo a riempire con il resto delle strategie

    }
    //method needed to be fixed: facciamo il singolo pick o direttamente i due pick delle carte qui?
    public static CommonGoalCard drawCommon() {
        Random random = new Random();
        int idx = random.nextInt(commonGoalCardDeck.size());

        return commonGoalCardDeck.get(idx);
    }

}
