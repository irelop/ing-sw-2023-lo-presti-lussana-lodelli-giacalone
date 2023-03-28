package it.polimi.ingsw;
import java.util.ArrayList;
import java.util.Scanner;

public class Player {
    private final String nickname;
    protected Shelf myShelfie;
    protected Score myScore;
    private PersonalGoalCard card;
    private boolean chair;
    private ArrayList<Tile> littleHand;//tiles in player's hand (the ones just picked)
    private boolean[] commonGoalsAchived;

    public Player(String nickname){
        this.nickname = nickname;
        this.myShelfie = new Shelf();
        this.myScore = new Score();
        this.chair = false;
        this.littleHand = new ArrayList<Tile>();
        this.commonGoalsAchived = new boolean[2];
        this.commonGoalsAchived[0] = false;
        this.commonGoalsAchived[1] = false;
    }

    public void setChair(){
        this.chair = true;
    }

    public boolean hasChair(){
        return this.chair;
    }

    public void setCommonGoalAchived(int indexCommonCard){
        this.commonGoalsAchived[indexCommonCard] = true;
    }


    public void setCard(PersonalGoalCard card){
        this.card = card;
    }

    public ArrayList<Tile> getLittleHand(){
        return this.littleHand;
    }

    public boolean isCommonGoalAchived(int indexCommonCard){
        return this.commonGoalsAchived[indexCommonCard];
    }

    @Override
    public String toString(){
        return nickname+" achived "+myScore.getScore()+" points";
    }


    //controllare che l'utente inserisca il numero corretto
    public void orderTiles(ArrayList<Tile> chosenTiles){
        if(chosenTiles.size() == 1){
            this.littleHand.add(chosenTiles.get(0));
            return;
        }

        int tilesNumber = chosenTiles.size();
        int[] choices = new int[tilesNumber];
        Scanner scanner = new Scanner(System.in);

        System.out.print("Your tiles are: ");
        for(int i=0; i<tilesNumber; i++){
            System.out.println((i+1)+ ") " + chosenTiles.get(i)); //va bene scrivere chosenTiles.get(i)? serve un override di tostring?
        }

        System.out.println("Choose the order (the first one is the lowest):");
        for(int i=0; i<tilesNumber; i++){
            choices[i] = scanner.nextInt();
        }

        for(int i=0; i<tilesNumber; i++){
            for(int j=0; j<tilesNumber; j++){
                if(choices[j] == tilesNumber-i){
                    int correctIndex = choices[j]-1;
                    this.littleHand.add(chosenTiles.get(correctIndex));
                    break;
                }
            }
        }

    }
}