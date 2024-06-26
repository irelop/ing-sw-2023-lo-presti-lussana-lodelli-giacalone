package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.chat.ChatMessage;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;
import java.time.LocalTime;

/**
 * ChatMsgRequest: this message wraps the chat message inserted by the user. It is forwarded to the server in order
 * to be properly processed.
 * @author Andrea Giacalone
 */
public class ChatMsgRequest extends C2SMessage{
    ChatMessage messageToSend;

    public ChatMsgRequest(String sender, String receiver, String content){
        LocalTime timeSent = LocalTime.now();

        this.messageToSend = new ChatMessage(sender.toUpperCase(),receiver.toUpperCase(),timeSent,content);
    }

    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.getController().updateChat(messageToSend);
    }



    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            server.getController(client).updateChat(messageToSend);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }


    }


}
