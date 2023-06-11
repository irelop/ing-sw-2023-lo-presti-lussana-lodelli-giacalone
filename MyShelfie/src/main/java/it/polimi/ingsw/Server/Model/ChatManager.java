package it.polimi.ingsw.Server.Model;
import java.util.ArrayList;

/**
 * ChatManager class: this class manages the chat among players in a game. It allows to send messages, check their
 *                    validity and filter them depending their privacy policy.
 *
 */
public class ChatManager {
    private ChatStorage commonChatPool;     //record of all messages in the pool
    private ArrayList<String> chatters;     //players of the game who can chat


    public ChatManager() {
        this.commonChatPool = new ChatStorage();
        this.chatters = new ArrayList<>();
    }


    /**
     * OVERVIEW: this method checks if the receiver of the message exists and eventually add this one to the pool
     *           storing the correct infos related.
     * @param messageToSend : the message to be sent.
     * @return true: if the receiver matches with one of the players
     *         false: otherwise
     */
    public boolean updateChat(ChatMessage messageToSend){
        if(messageToSend.getReceiver().equals("EVERYONE")){
            commonChatPool.addMessage(messageToSend);
            return true;
        }else if(chatters.stream().filter(x->x.equals(messageToSend.getReceiver())).count()==1){
            commonChatPool.addMessage(messageToSend);
            return true;
        }
        else return false;
    }

    /**
     * OVERVIEW: this method allows to add a chatter.
     * @param player: the nickname of the player.
     */
    public void addChatter(String player){
        this.chatters.add(player);
    }

    /**
     * OVERVIEW: this method allows to get a snapshot of the record of messages filtering the public messages and the private ones
     *           redirected to the requester.
     * @param requester: the nickname of the player who asks to chat.
     * @return customChat: the snapshot of the record of messages
     */
    public ChatStorage getCustomChat(String requester){
        ChatStorage customChat = new ChatStorage();
        for(int i = 0; i< commonChatPool.getStorage().size(); i++) {
            if ((commonChatPool.getStorage().get(i).getReceiver().equals(new String(requester))) || (commonChatPool.getStorage().get(i).getReceiver().equals("EVERYONE"))) {
                customChat.addMessage(commonChatPool.getStorage().get(i));
            }
        }

        return customChat;
    }

}
