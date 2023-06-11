package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class ChatRecordRequest extends C2SMessage{

    private String requester;

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
