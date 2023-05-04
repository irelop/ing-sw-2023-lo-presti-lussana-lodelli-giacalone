package it.polimi.ingsw.Server.Messages;

/**
 * Message that allow the player to see the ChooseTilesFromBoardView.
 *
 * @author Irene Lo Presti
 */

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ChooseTilesFromBoardView;
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
        this.boardSnapshot = boardSnapshot;
        this.personalGoalCard = personalGoalCard;
        this.commonGoalCards = commonGoalCards;
        this.turnNumber = turnNumber;
        this.playersNames = playersNames;
    }


    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(new ChooseTilesFromBoardView(this));
    }
}
