package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class FinishGameRequest extends C2SMessage{

    public String nickname;

    public FinishGameRequest(String nickname){
        this.nickname = nickname;
    }

    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.getController().finishGame(clientHandler, nickname);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.getController(client).finishGameRMI(client, nickname);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
