package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.Model.Tile;

public class ChooseDirectionAndNumberOfTilesMsg extends S2CMessage{

    public String nickname;
    public int maxTilesPickable;
    public Tile[][] boardSnapshot;
    public int initialRow;
    public int initialColumn;

    public ChooseDirectionAndNumberOfTilesMsg(String nickname, int maxTilesPickable, Tile[][] boardSnapshot, int initialRow, int initialColumn){
        this.nickname = nickname;
        this.maxTilesPickable = maxTilesPickable;
        this.boardSnapshot = boardSnapshot;
        this.initialRow = initialRow;
        this.initialColumn = initialColumn;
    }

    @Override
    public void processMessage(ServerHandler serverHandler){}
}
