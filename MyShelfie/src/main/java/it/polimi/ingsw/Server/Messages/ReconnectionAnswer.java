package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.view.CLI.LoginView;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

/**
 * ReconnectionAnswer: this message wraps the answer from the server regarding the result of the user reconnection
 * request. There are four possible cases:
 * 1) if the nickname inserted matches with a player actually connected and currently playing.
 * 2) if the nickname matches with a player previously connected to a game not already started.
 * 3) if the nickname doesn't match with any of the players previously connected to the game.
 * 4) if the reconnection request, despite being valid, was too late and in the meanwhile, the game has already finished.
 * 5) if the request is valid and the player can reconnect to his/her game.
 *
 * @authors Irene Lo Presti, Andrea Giacalone
 */
public class ReconnectionAnswer extends S2CMessage{


    public String msg;
    public String nickname;


    public ReconnectionAnswer(String msg,String nickname){
        this.msg = msg;
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        if(serverHandler.getOwner().gui) {
            serverHandler.getOwner().getStageManager().getController().receiveAnswer(this);
        } else {
            serverHandler.getOwner().setNickname(nickname);     //re-setting the nickname
            LoginView loginView = (LoginView) serverHandler.getOwner().getCurrentView();
            loginView.setReconnectionAnswer(this);
            serverHandler.getOwner().getCurrentView().notifyView();
        }
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client) {
        try {
            if(client.getOwner().gui) {
                client.getOwner().getStageManager().getController().receiveAnswer(this);
            } else {
                client.getOwner().setNickname(nickname);     //re-setting the nickname
                LoginView loginView = (LoginView) client.getOwner().getCurrentView();
                loginView.setReconnectionAnswer(this);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
