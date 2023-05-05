package it.polimi.ingsw.Server.Model;

/**
 * Player class: this class contains all the information about a player: nickname, score, shelf,
 * personal goal card, the tiles in his/her hand chosen from the board, if he/she has achieved some
 * common goal and if he/she has the chair.
 *
 * @authors Irene Lo Presti, Matteo Lussana
 */

import it.polimi.ingsw.Server.Model.Exceptions.InvalidTileIndexInLittleHandException;

import java.util.ArrayList;

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
     * @param nickname : nickname chosen by the player
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
     * OVERVIEW: getter methof
     * @return nickname
     */
    public String getNickname(){
        return nickname;
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
     * @param indexCommonCard : index of the common goal card (0 or 1)
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

    public void setLittleHand(ArrayList<Tile> tilesChosen){
        if(littleHand.size()==0)
            littleHand.addAll(tilesChosen);
        else{
            littleHand.clear();
            littleHand.addAll(tilesChosen);
        }
    }

    /**
     * OVERVIEW: getter method
     * @see Tile
     * @return littleHand
     */
    public ArrayList<Tile> getLittleHand(){
        return littleHand;
    }

    /**
     * OVERVIEW: getter method
     * @param indexCommonCard : number of the common goal card (0 or 1)
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
    public void orderTiles(ArrayList<Tile> chosenTiles, int[] choices) {
        // if the player chooses only one tile, there is no need to order.
/*
        if(chosenTiles.size() == 1){
            this.littleHand.add(chosenTiles.get(0));
            return;
        }

        for(int j=0; j<chosenTiles.size(); j++){
            for(int k=0; k<chosenTiles.size(); k++){
                if(choices[k] == j+1 ){
                    littleHand.add(chosenTiles.get(k));
                    break;
                }
            }
        }
*/
        if (chosenTiles.size() == 1)
            return;
        System.out.println("Before order " + littleHand);
        for (int idx : choices) {
            littleHand.add(chosenTiles.get(idx));
        }
        System.out.println("Before removing " + littleHand);
        for (int i : choices) {
            // remove head #choises times
            littleHand.remove(0);
        }
        System.out.println("After order " + littleHand);

        //da vedere con 3 tiles prima dobbiamo sbloccare gli altri
    }

    /**
     * OVERVIEW: this method manage the user input of order
     * @deprecated
     * @see Tile
     * @param chosenTiles : ArrayList of the tiles that the player has chosen from the board
     */
    public int[] askOrder(ArrayList<Tile> chosenTiles){

        int tilesNumber = chosenTiles.size();
        int[] choices = new int[tilesNumber];

        if(chosenTiles.size()==1){
            choices[0] = 1;
            return choices;
        }
        System.out.print("Your tiles are: ");
        for(int i=0; i<tilesNumber; i++)
            System.out.println((i+1)+ ") " + chosenTiles.get(i));

        System.out.println("Choose the order (the first one is the lowest):");
        try{
            getTiles(choices);
        }catch(InvalidTileIndexInLittleHandException e){
            System.out.println(e);
        }
        return choices;
    }


    /**
     * OVERVIEW: this method gets the right order
     * @param choices : array of the indexes that represent the chosen tiles
     * @throws InvalidTileIndexInLittleHandException if the player chooses a number that is not between
     * 1 and the number of the tiles that he/she has chosen from the board or if he/she has already chosen it
     */
    public void getTiles(int[] choices) throws InvalidTileIndexInLittleHandException {

        for(int i=0; i<choices.length; i++)
            if (choices[i] < 0 || choices[i] >= choices.length)
                throw new InvalidTileIndexInLittleHandException(choices.length);
        for(int i=0; i<choices.length-1; i++){
            for(int j=i+1; j<choices.length; j++){
                if(choices[i]==choices[j]) throw new InvalidTileIndexInLittleHandException(choices.length);
            }
        }
    }
}
