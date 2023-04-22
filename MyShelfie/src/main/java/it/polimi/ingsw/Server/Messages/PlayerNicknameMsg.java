package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Server.ClientHandler;

/**
 * This class creates a message with the player's nickname and, it sends it to the client
 * in order to have a more clear output.
 *
 * @author Irene Lo Presti
 */

public class PlayerNicknameMsg extends S2CMessage{

    public String nickname;

    /**
     * OVERVIEW: constructor method, given the player it creates the message with his/her nickname
     * @see Player
     * @param player is the Player who is playing
     */
    public PlayerNicknameMsg(Player player){
        this.nickname = player.getNickname();
    }

    /**
     * OVERVIEW: this method sends the message to the client (with the client handler)
     * @see ClientHandler
     * @param clientHandler is the instance of ClientHandler
     */
    @Override
    public void processMessage(ClientHandler clientHandler) {
        clientHandler.sendMessageToClient(this);
    }



}
