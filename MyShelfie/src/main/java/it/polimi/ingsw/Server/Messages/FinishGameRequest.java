package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class FinishGameRequest extends C2SMessage{
    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.getController().finishGame(clientHandler);
    }

    //da sistemare quando capiamo come chiudere la connessione RMI
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            server.getController().finishGameRMI(client);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
