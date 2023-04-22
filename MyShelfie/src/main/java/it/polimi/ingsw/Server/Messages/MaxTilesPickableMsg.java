package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Server.ClientHandler;

/**
 * This class creates a message with the maximum number of tiles pickable by the player, and it sends it to
 * the client
 *
 * @author Irene Lo Presti
 */

public class MaxTilesPickableMsg extends S2CMessage{

    public int maxTilesPickable;

    /**
     * OVERVIEW: constructor method, it creates the message with the
     * maximum number of tiles pickable by the player
     * @param maxTilesPickable: maximum number of tiles pickable by the player
     */
    public MaxTilesPickableMsg(int maxTilesPickable){
        this.maxTilesPickable = maxTilesPickable;
    }

    /**
     * OVERVIEW: this method sends the message to the client with the client handler
     * @param clientHandler: instance of the ClientHandler
     */
    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.sendMaxTilesPickable(this);
    }
}
