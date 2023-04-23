package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.ClientHandler;

/**
 * This class creates a message with the direction and the number of tiles chosen by the player and
 * it sends it to the server
 *
 * @author Irene Lo Presti
 */

public class DirectionAndNumberOfTilesMsg extends C2SMessage{

    public char direction;
    public int numberOfTiles;

    /**
     * OVERVIEW: constructor method, it creates the message with the direction and the number of tiles
     * @param direction: char that represent the chosen direction
     * @param numberOfTiles: number of tiles chosen
     */
    public DirectionAndNumberOfTilesMsg(char direction, int numberOfTiles){
        this.direction = direction;
        this.numberOfTiles = numberOfTiles;
    }

    /**
     * OVERVIEW: this method sends the message to the server with the server handler
     * @see ClientHandler
     * @param clientHandler: instance of the ServerHandler
     */
    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.sendMessageToServer(this);
    }
}
