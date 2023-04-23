package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Model.Board;
import it.polimi.ingsw.Model.Tile;

/**
 * This class creates a message with a snapshot of the board and it sends it to the client
 *
 * @author Irene Lo Presti
 */

public class BoardMsg extends S2CMessage{

    public Tile[][] boardSnapshot;

    /**
     * OVERVIEW: constructor method, it creates the message with the snapshot of the board
     * @see Board
     * @param Board: instance of board
     */
    public BoardMsg(Board Board){
        this.boardSnapshot = Board.getBoardGrid();
    }

    /**
     * OVERVIEW: this method sends the message to the server with the server handler
     * @see ServerHandler
     * @param serverHandler instance of ServerHandler
     */
    @Override
    public void processMessage(ServerHandler serverHandler){
        serverHandler.sendMessageToClient(this);
    }
}
