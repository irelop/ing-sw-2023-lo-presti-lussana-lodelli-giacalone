package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

/**
 * Class for a generic message send on a network link from the Client to the Server
 *
 * @author Irene Lo Presti
 */
public abstract class C2SMessage extends NetworkMessage{
    public abstract void processMessage(ClientHandler clientHandler);

    public abstract void processMessage(RemoteInterface server, RemoteInterface client);
}
