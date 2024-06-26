package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.utils.rmi.RemoteInterface;
import java.rmi.RemoteException;

/**
 * RMIServerHandler class: an implementation of the abstract class ServerHandler in order to manage the network functions
 *                            used in an RMI communication.
 * @author Andrea Giacalone, Irene Lo Presti
 */

public class RMIServerHandler extends ServerHandler{
    private  RemoteInterface remoteServer;   //reference to the remote interface of the server for RMI connection
    private boolean stop;
    public RMIServerHandler(Client owner,RemoteInterface remoteServer) {
        super(owner);
        this.stop = false;
        this.remoteServer = remoteServer;
    }

    /**
     * OVERVIEW: main method of the class - it allows to periodically ping the remote server in order to check for
     *           possible disconnections and eventually to manage them safely.
     */
    @Override
    public void run() {
        while(!stop){
            try{
                Thread.sleep(1000);
                if(getRemoteServer() != null)
                    getRemoteServer().ping();
                else {
                    stop = true;
                    System.out.println("[RMI] Error: connection to the server has been lost");
                    System.out.println("Disconnecting...");
                    System.exit(1);
                }
            }catch(RemoteException e){
                 //goOn= false;
                stop = true;
                System.out.println("[RMI] Error: connection to the server has been lost");
                System.out.println("Disconnecting...");
                System.exit(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop(){
        try {
            stop = true;
            this.remoteServer.disconnectRemoteClient(getOwner().getRemoteClient());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        this.remoteServer = null;

        System.out.println("[RMI] Connection with RMI server closed");
    }


    @Override
    public void sendMessageToServer(C2SMessage msg) {
        try {
            getRemoteServer().sendMessageToServer(msg,getOwner().getRemoteClient());
        } catch (RemoteException e) {
            System.out.println("[RMI] Communication error: problems in sending message to the server");
        }
    }

    //- - - - - - - G E T T E R S- - - - - - -
    public RemoteInterface getRemoteServer() {
        return remoteServer;
    }
}
