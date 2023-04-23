package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.ClientHandler;

import java.io.IOException;

/**
 * This class creates a message with the initial row and the initial column chosen by the player,
 * and it sends it to the server.
 *
 * @author Irene Lo Presti
 */

public class InitialPositionMsg extends C2SMessage{

    public int row;
    public int column;

    /**
     * OVERVIEW: constructor method, it creates the message with the initial row and the initial column
     * @param row: int for the initial row
     * @param column: int for the initial column
     */
    public InitialPositionMsg(int row, int column){
        this.row = row;
        this.column = column;
    }

    /**
     * OVERVIEW: this method sends the message to the server with the server handler
     * @see ClientHandler
     * @param clientHandler instance of ServerHandler
     */
    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.sendMessageToServer(this);
    }
}
