package it.polimi.ingsw.Server.Messages;
import java.io.Serializable;
import java.util.UUID;

/**
 * Class for a generic message send on a network link
 *
 * @author Irene Lo Presti
 */

public class NetworkMessage implements Serializable {
    UUID ID = UUID.randomUUID();

    public UUID getID()
    {
        return ID;
    }
}
