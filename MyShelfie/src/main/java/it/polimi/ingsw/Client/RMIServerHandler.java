package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

/**
 * RMIServerHandler class: an implementation of the abstract class ServerHandler in order to manage the network functions
 *                            used in a RMI communication.
 */

public class RMIServerHandler extends ServerHandler{
    private final RemoteInterface remoteServer;   //reference to the remote interface of the server for RMI connection
    public RMIServerHandler(Client owner,RemoteInterface remoteServer) {
        super(owner);
        this.remoteServer = remoteServer;
    }

    /**
     * OVERVIEW: main method of the class - it allows to periodically ping the remote server in order to check for
     *           possible disconnections and eventually to manage them safely.
     */
    @Override
    public void run() {
        boolean goOn = true;
        while(goOn){
            try{
                Thread.sleep(1000);
                getRemoteServer().ping();
            }catch(RemoteException e){
                 goOn= false;
                System.out.println("[ERROR] Connection to the server has been lost");
                System.out.println("Disconnecting...");
                System.exit(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void sendMessageToServer(C2SMessage msg) {
        try {
            getRemoteServer().sendMessageToServer(msg,getOwner().getClient());
        } catch (RemoteException e) {
            System.out.println("[RMI] Communication error: problems in sending message to the server");
        }
    }

    //- - - - - - - G E T T E R S- - - - - - -
    public RemoteInterface getRemoteServer() {
        return remoteServer;
    }
}
