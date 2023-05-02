package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ChooseTilesFromBoardView;
import it.polimi.ingsw.Server.Model.CommonGoalCard;
import it.polimi.ingsw.Server.Model.PersonalGoalCard;
import it.polimi.ingsw.Server.Model.Tile;

public class YourTurnMsg extends S2CMessage{

    public String nickname;
    public int maxTilesPickable;
    public Tile[][] boardSnapshot;
    public CommonGoalCard[] commonGoalCards;
    public PersonalGoalCard personalGoalCard;

    public YourTurnMsg(String nickname, int maxTilesPickable, Tile[][] boardSnapshot,
                       CommonGoalCard[] commonGoalCards, PersonalGoalCard personalGoalCard){
        this.nickname = nickname;
        this.maxTilesPickable = maxTilesPickable;
        this.boardSnapshot = boardSnapshot;
        this.personalGoalCard = personalGoalCard;
        this.commonGoalCards = commonGoalCards;
    }


    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(new ChooseTilesFromBoardView(this));
    }
}
