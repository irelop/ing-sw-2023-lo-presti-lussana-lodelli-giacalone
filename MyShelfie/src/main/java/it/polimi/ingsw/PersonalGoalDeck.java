package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Random;

public class PersonalGoalDeck {

    private ArrayList<PersonalGoalCard> personalGoalCardDeck;

    public PersonalGoalDeck() {
        personalGoalCardDeck = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            personalGoalCardDeck.add(new PersonalGoalCard());
        }
    }

    public PersonalGoalCard draw() {
        Random random = new Random();
        int idx = random.nextInt(personalGoalCardDeck.size());
        return personalGoalCardDeck.get(idx);
    }
}
