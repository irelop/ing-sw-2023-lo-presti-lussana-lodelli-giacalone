package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Client.ServerHandler;
import java.io.IOException;

/**
 * Class for a generic message send on a network link from the Client to the Server
 *
 * @author Irene Lo Presti
 */



public abstract class C2SMessage extends NetworkMessage{
    public abstract void processMessage(ServerHandler serverHandler);
}
