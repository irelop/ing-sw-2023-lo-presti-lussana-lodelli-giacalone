package it.polimi.ingsw;

/**
 * Player class: this class contains all the information about a player: nickname, score, shelf,
 * personal goal card, the tiles in his/her hand chosen from the board, if he/she has achieved some
 * common goal and if he/she has the chair.
 *
 * @authors Irene Lo Presti, Matteo Lussana
 */

import it.polimi.ingsw.Exceptions.InvalidTileIndexInLittleHandException;
import java.util.ArrayList;
import java.util.Scanner;

public class Player {
    private final String nickname;
    protected Shelf myShelfie;
    protected Score myScore;
    private PersonalGoalCard card;
    private boolean chair;
    private ArrayList<Tile> littleHand;//tiles in player's hand (the ones just picked)
    private boolean[] commonGoalsAchieved;

    /**
     * OVERVIEW: Constructor: it is used to when a player joins the game. This method saves the nickname and
     * initializes all the attributes
     * @see Shelf
     * @see Tile
     * @see Score
     * @param nickname : String
     */
    public Player(String nickname){
        this.nickname = nickname;
        this.myShelfie = new Shelf();
        this.myScore = new Score();
        this.chair = false;
        this.littleHand = new ArrayList<>();
        this.commonGoalsAchieved = new boolean[2];
        this.commonGoalsAchieved[0] = false;
        this.commonGoalsAchieved[1] = false;
    }

    /**
     * OVERVIEW: getter method
     * @see PersonalGoalCard
     * @return card
     */
    public PersonalGoalCard getPersonalGoalCard(){
        return this.card;
    }

    /**
     * OVERVIEW: setter method for chair. It is called only if the player is randomly chosen to be the first
     * to play.
     */
    public void setChair(){
        this.chair = true;
    }

    /**
     * OVERVIEW: getter method
     * @return chair
     */
    public boolean hasChair(){
        return this.chair;
    }

    /**
     * OVERVIEW: setter method for commonGoalAchived. It is called only if the player achieves a common goal.
     * @see CommonGoalCard
     * @param indexCommonCard : int
     */
    public void setCommonGoalAchieved(int indexCommonCard){
        this.commonGoalsAchieved[indexCommonCard] = true;
    }

    /**
     * OVERVIEW: setter method for personalGoalCard. This method is used to save the personal goal card after
     * the player draws it from the deck.
     * @see PersonalGoalCard
     * @param card : PersonalGoalCard
     */
    public void setCard(PersonalGoalCard card){
        this.card = card;
    }

    /**
     * OVERVIEW: getter method
     * @see Tile
     * @return littleHand
     */
    public ArrayList<Tile> getLittleHand(){
        return this.littleHand;
    }

    /**
     * OVERVIEW: getter method
     * @param indexCommonCard : int
     * @return commonGoalAchieved[indexCommonCard]
     */
    public boolean isCommonGoalAchieved(int indexCommonCard){
        return this.commonGoalsAchieved[indexCommonCard];
    }

    @Override
    public String toString(){
        return nickname+" achived "+myScore.getScore()+" points";
    }


    /**
     * OVERVIEW: this method orders the chosen tiles from the board in order to put them in the player's shelf
     * @see Tile
     * @param chosenTiles : ArrayList<Tile>
     */

    //bisogna rivedere la funzione orderTiles, per come è fatta è difficile da testare
    // IDEA: spostiamo al di fuori della funzione tutta la richiesta del nuovo ordine delle tessere,
    // e quindi orderTiles riceverà chosenTiles (la lista delle tessere prese dalla board) e
    // e choices (un array contenente l'ordine scelto dall'utente)
    //
    // in modo tale che durante il test io possa simulare un ordine scelto da un utente
    public void orderTiles(ArrayList<Tile> chosenTiles) {
        // if the player chooses only one tile, there is no need to order.
        if(chosenTiles.size() == 1){
            this.littleHand.add(chosenTiles.get(0));
            return;
        }

        int tilesNumber = chosenTiles.size();
        int[] choices = new int[tilesNumber];

        System.out.print("Your tiles are: ");
        for(int i=0; i<tilesNumber; i++){
            System.out.println((i+1)+ ") " + chosenTiles.get(i));

        System.out.println("Choose the order (the first one is the lowest):");
        try{
            getTiles(choices);
        }catch(InvalidTileIndexInLittleHandException e){
            System.out.println(e);
        }


        for(int j=0; j<tilesNumber; j++){
            for(int k=0; k<tilesNumber; k++){
                if(choices[k] == tilesNumber-j){
                    int correctIndex = choices[k]-1;
                    this.littleHand.add(chosenTiles.get(correctIndex));
                    break;
                }
            }
        }

    }
}

    /**
     * OVERVIEW: this method gets the new order
     * @deprecated
     * @param choices : int[]
     * @throws InvalidTileIndexInLittleHandException e
     */
    private void getTiles(int[] choices) throws InvalidTileIndexInLittleHandException {
        Scanner scanner = new Scanner(System.in);

        for(int i=0; i<choices.length; i++){
            choices[i] = scanner.nextInt();
            if(choices[i]<0 || choices[i]> choices.length) throw new InvalidTileIndexInLittleHandException(choices.length);
        }
        for(int i=0; i< choices.length-1; i++){
            for(int j=i+1; j<choices.length; j++){
                if(choices[i]==choices[j]) throw new InvalidTileIndexInLittleHandException(choices.length);
            }
        }
    }
}
