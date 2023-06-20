package it.polimi.ingsw.Server.chat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ChatStorage class: it represents a buffer of chat messages collecting all the messages of the chat in game.
 */
public class ChatStorage implements Serializable {

    private ArrayList<ChatMessage> storage;

    public ChatStorage() {
        this.storage = new ArrayList<>();
    }

    /**
     * OVERVIEW: it allows to add a message to the buffer.
     * @param chatMessage: message to add.
     */
    public void addMessage(ChatMessage chatMessage){
        storage.add(chatMessage);
    }

    public ArrayList<ChatMessage> getStorage() {
        return storage;
    }
}
