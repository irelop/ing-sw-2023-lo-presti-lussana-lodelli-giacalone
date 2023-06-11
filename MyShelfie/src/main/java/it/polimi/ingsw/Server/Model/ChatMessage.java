package it.polimi.ingsw.Server.Model;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * ChatMessage class: it represents the structure of a message of a chatter, wrapping all useful infos as the sender,
 *                    the receiver, a timestamp of the sending and its content.
 */
public class ChatMessage implements Serializable {
    private String sender;
    private String receiver;
    private LocalTime localTime;
    private String content;

    public ChatMessage(String sender, String receiver, LocalTime localTime, String content) {
        this.sender = sender.toUpperCase();
        this.receiver = receiver.toUpperCase();
        this.localTime = localTime;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }


    public String getReceiver() {
        return receiver;
    }


    public LocalTime getLocalTime() {
        return localTime;
    }


    public String getContent() {
        return content;
    }

}
