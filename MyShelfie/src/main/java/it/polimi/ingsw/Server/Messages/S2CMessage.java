package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.io.IOException;

/**
 * Class for a generic message send on a network link from the Server to the Client
 *
 * @author Irene Lo Presti
 */

public abstract class S2CMessage extends NetworkMessage{
    public abstract void processMessage(ServerHandler serverHandler);

    public abstract void processMessage(RemoteInterface server, RemoteInterface client);
}
