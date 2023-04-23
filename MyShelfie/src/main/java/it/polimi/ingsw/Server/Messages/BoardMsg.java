package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Model.Board;
import it.polimi.ingsw.Model.Tile;

public class BoardMsg extends S2CMessage{

    public Tile[][] boardSnapshot;

    public BoardMsg(Board Board){
        this.boardSnapshot = Board.getBoardGrid();
    }

    @Override
    public void processMessage(ServerHandler serverHandler){
        serverHandler.sendMessageToClient(this);
    }
}
