package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ChooseInitialPositionView;
import it.polimi.ingsw.Server.Model.Tile;

public class YourTurnMsg extends S2CMessage{

    public String nickname;
    public int maxTilesPickable;
    public Tile[][] boardSnapshot;

    public YourTurnMsg(String nickname, int maxTilesPickable, Tile[][] boardSnapshot){
        this.nickname = nickname;
        this.maxTilesPickable = maxTilesPickable;
        this.boardSnapshot = boardSnapshot;
    }


    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(new ChooseInitialPositionView(this));
    }
}
