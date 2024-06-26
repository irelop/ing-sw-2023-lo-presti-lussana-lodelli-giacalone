package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.utils.Exceptions.InvalidTileIndexInLittleHandException;
import java.util.ArrayList;

/**
 * Player class: this class contains all the information about a player: nickname, score, shelf,
 * personal goal card, the tiles in his/her hand chosen from the board, if he/she has achieved some
 * common goal and if he/she has the chair.
 *
 * @author Irene Lo Presti, Matteo Lussana
 */
public class Player {
    private final String nickname;
    private Shelf myShelfie;
    private Score myScore;
    private PersonalGoalCard card;
    private boolean chair;
    private ArrayList<Tile> littleHand;//tiles in player's hand (the ones just picked)
    private boolean[] commonGoalsAchieved;
    private boolean hasFinished;

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
        this.hasFinished = false;
    }
    public void clearLittleHand(){
        littleHand.clear();
    }

    /**
     * OVERVIEW: this method orders the tiles of the little hand
     * @see Tile
     * @param order: this array contains the index of the tiles in the correct order
     */
    public void orderTiles(int[] order){

        // if the player chooses only one tile, there is no need to order
        if(littleHand.size() == 1)
            return;

        Tile[] chosenTiles = new Tile[littleHand.size()];
        for(int i=0; i< littleHand.size(); i++)
            chosenTiles[i]=littleHand.get(i);

        for(int i=0; i<littleHand.size(); i++){
            littleHand.set(i,chosenTiles[order[i]]);
        }

    }


    /**
     * OVERVIEW: this method gets the right order
     * @deprecated
     * @param choices : array of the indexes that represent the chosen tiles
     * @throws InvalidTileIndexInLittleHandException if the player chooses a number that is not between
     * 1 and the number of the tiles that he/she has chosen from the board or if he/she has already chosen
     * it. Used for testing
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

    public void addScore(int score){
        myScore.addScore(score);
    }

    //- - - - - - - - - - - - - - - -| SETTER METHODS |- - - - - - - - - - - - - - - - - - - - - - - -
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
    public void setScore(int score){
        this.myScore.addScore(score);
    }

    public void setShelf(Tile[][] shelf){
        myShelfie = new Shelf(shelf);
    }

    public void setLittleHand(ArrayList<Tile> tilesChosen){
        if(littleHand.size()==0)
            littleHand.addAll(tilesChosen);
        else{
            clearLittleHand();
            littleHand.addAll(tilesChosen);
        }
    }
    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
    }
    /**
     * It is called only if the player is randomly chosen to be the first to play.
     */
    public void setChair(){
        this.chair = true;
    }
    //- - - - - - - - - - - - - - - -| GETTER METHODS |- - - - - - - - - - - - - - - - - - - - - - - -

    public Shelf getShelf() {
        return myShelfie;
    }

    public int getScore(){
        return myScore.getScore();
    }

    public Tile[][] getShelfGrid(){
        return myShelfie.getGrid();
    }
    public boolean getHasFinished(){
        return hasFinished;
    }
    public String getNickname(){
        return nickname;
    }
    public ArrayList<Tile> getLittleHand(){
        return littleHand;
    }
    /**
     *
     * @param indexCommonCard : number of the common goal card (0 or 1)
     * @return commonGoalAchieved[indexCommonCard]
     */
    public boolean isCommonGoalAchieved(int indexCommonCard){
        return this.commonGoalsAchieved[indexCommonCard];
    }
    public PersonalGoalCard getPersonalGoalCard(){
        return this.card;
    }
    public boolean hasChair(){
        return this.chair;
    }

}
