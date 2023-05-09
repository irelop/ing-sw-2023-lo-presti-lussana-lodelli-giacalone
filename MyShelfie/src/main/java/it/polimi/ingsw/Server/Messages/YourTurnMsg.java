package it.polimi.ingsw.Server.Messages;

/**
 * Message that allow the player to see the ChooseTilesFromBoardView.
 *
 * @author Irene Lo Presti
 */

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ChooseTilesFromBoardView;
import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.CommonGoalCard;
import it.polimi.ingsw.Server.Model.PersonalGoalCard;
import it.polimi.ingsw.Server.Model.Tile;

import java.util.ArrayList;

public class YourTurnMsg extends S2CMessage{

    public String nickname;
    public int maxTilesPickable;
    public Tile[][] boardSnapshot;
    public CommonGoalCard[] commonGoalCards;
    public PersonalGoalCard personalGoalCard;
    public int turnNumber;
    public ArrayList<String> playersNames;

    public YourTurnMsg(String nickname, int maxTilesPickable, Tile[][] boardSnapshot,
                       CommonGoalCard[] commonGoalCards, PersonalGoalCard personalGoalCard, int turnNumber,
                        ArrayList<String> playersNames){
        this.nickname = nickname;
        this.maxTilesPickable = maxTilesPickable;
        this.boardSnapshot = new Tile[9][9];

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                this.boardSnapshot[i][j] = boardSnapshot[i][j];

        this.personalGoalCard = personalGoalCard;
        this.commonGoalCards = commonGoalCards;
        this.turnNumber = turnNumber;
        this.playersNames = playersNames;
    }


    @Override
    public void processMessage(ServerHandler serverHandler) {

        serverHandler.getClient().transitionToView(new ChooseTilesFromBoardView(this));
        serverHandler.getClient().getCurrentView().notifyView();
    }

    public String toString(){
      return "de sono un messaggio eeenoooorme";
    }
}
