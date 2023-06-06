package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class RMIServerHandler extends ServerHandler{
    private RemoteInterface remoteServer;
    public RMIServerHandler(Client owner,RemoteInterface remoteServer) {
        super(owner);
        this.remoteServer = remoteServer;
    }

    @Override
    public void run() {
        boolean goOn = true;
        while(goOn){
            try{
                Thread.sleep(1000);
                remoteServer.ping();
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
}
