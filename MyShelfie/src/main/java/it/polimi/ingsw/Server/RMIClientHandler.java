package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Model.MyShelfie;

import java.rmi.RemoteException;

public class RMIClientHandler extends ClientHandler {

    public RMIClientHandler(MyShelfie game, RemoteInterface remoteClient){
        super(game, remoteClient);
        setIsRMI(true);
    }
}
