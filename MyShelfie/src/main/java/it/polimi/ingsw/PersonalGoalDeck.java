package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Random;

/**
 * Draft of class PersonalGoalCardDeck: this version wraps the base structure and a method that allows to randomly
 * draw a common goal card among the possible ones.
 */
public class PersonalGoalDeck {

    private ArrayList<PersonalGoalCard> personalGoalCardDeck;
    private final static int MAX_SIZE = 12;

    public PersonalGoalDeck() {
        personalGoalCardDeck = new ArrayList<>();

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("PersonalGoalCards.txt");

        for (int i = 0; i < MAX_SIZE; i++) {
            personalGoalCardDeck.add(new PersonalGoalCard());
        }
    }

    public PersonalGoalCard drawPersonal() {
        Random random = new Random();
        int idx = random.nextInt(personalGoalCardDeck.size());
        return personalGoalCardDeck.get(idx);
    }
}
