package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Model.Player;

public class PlayerNicknameMsg extends S2CMessage{

    public String nickname;

    public PlayerNicknameMsg(Player player){
        this.nickname = player.getNickname();
    }



}
