package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Model.MyShelfie;

public class RMIClientHandler extends ClientHandler {

    public RMIClientHandler(MyShelfie game, RemoteInterface remoteClient){
        super(game, remoteClient);
        setIsRMI(true);
    }

}
