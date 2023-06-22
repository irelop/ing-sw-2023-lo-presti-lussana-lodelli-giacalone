package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * ChatRecordRequest: this message allows to properly send a request to the server in order to return to the chatter
 * requester the record of chat messages currently available.
 * @author Andrea Giacalone
 */
public class ChatRecordRequest extends C2SMessage{

    private String requester; //the chatter who wants to see the chat record

    public ChatRecordRequest(String requester){
        this.requester = requester;
    }
    @Override
    public void processMessage(ClientHandler clientHandler) {
       clientHandler.getController().getCustomChat(requester);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            server.getController(client).getCustomChat(requester);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
}
