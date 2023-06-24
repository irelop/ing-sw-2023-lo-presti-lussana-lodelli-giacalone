package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.utils.ReadFileByLines;

import java.util.ArrayList;
import java.util.Random;

/**
 * PersonalGoalCardDeck class: this class wraps the game deck of personal goal cards and provides methods to allow the random
 * drawing process.
 *
 * @author Riccardo Lodelli
 */
public class PersonalGoalDeck {

    private ArrayList<PersonalGoalCard> personalGoalCardDeck;
    private final static int MAX_SIZE = 12;

    public PersonalGoalDeck() {
        personalGoalCardDeck = new ArrayList<>();

        ReadFileByLines reader = new ReadFileByLines();
        //reader.readFrom("MyShelfie/src/txtfiles/PersonalGoalCards.txt");
        reader.readFrom("src/txtfiles/PersonalGoalCards.txt");

        for (int i = 0; i < MAX_SIZE; i++) {
            personalGoalCardDeck.add(new PersonalGoalCard());
        }
    }

    /**
     * OVERVIEW: this method allows to draw a personal goal card from the deck.
     * @return a random chosen PersonalGoalCard form the 12 created.
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

    /**
     * OVERVIEW: this method allows to find the personal goal card associated to the given id.
     * @param id: the ID of the card to be found.
     * @return if exists the matching card.
     */
    public PersonalGoalCard getCard(String id){
        for(PersonalGoalCard card : personalGoalCardDeck){
            if(card.getId().equals(id))
                return card;
        }
        return null;
    }
}
