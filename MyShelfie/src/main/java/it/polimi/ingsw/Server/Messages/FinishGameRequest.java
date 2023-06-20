package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.utils.rmi.RemoteInterface;

import java.rmi.RemoteException;

public class FinishGameRequest extends C2SMessage{

    //public String nickname;

    /*public FinishGameRequest(){
        this.nickname = nickname;
    }*/

    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.getController().finishGame(clientHandler);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
       try {
            server.getController(client).finishGameRMI(client);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
