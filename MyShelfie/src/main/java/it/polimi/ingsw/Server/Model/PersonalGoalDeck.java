package it.polimi.ingsw.Server.Model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Draft of class PersonalGoalCardDeck: this version wraps the base structure and a method that allows to randomly
 * draw a common goal card among the possible ones.
 *
 * @author Riccardo Lodelli
 */
public class PersonalGoalDeck {

    private ArrayList<PersonalGoalCard> personalGoalCardDeck;
    private final static int MAX_SIZE = 12;

    public PersonalGoalDeck() {
        personalGoalCardDeck = new ArrayList<>();

        ReadFileByLines reader = new ReadFileByLines();
        reader.readFrom("MyShelfie/src/txtfiles/PersonalGoalCards.txt");
        //reader.readFrom("src/txtfiles/PersonalGoalCards.txt");

        for (int i = 0; i < MAX_SIZE; i++) {
            personalGoalCardDeck.add(new PersonalGoalCard());
        }
    }

    /**
     * Draw a card
     * @return a random chosen PersonalGoalCard form the 12 created
     */
    public PersonalGoalCard drawPersonal() {
        Random random = new Random();
        int idx = random.nextInt(personalGoalCardDeck.size());
        return personalGoalCardDeck.remove(idx);
    }

    public int getCurrentSize() {
        return personalGoalCardDeck.size();
    }

    public ArrayList<PersonalGoalCard> getPersonalGoalCardDeck() {
        return personalGoalCardDeck;
    }
}
