package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.Messages.C2SMessage;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;

public class RMIServerHandler extends ServerHandler{
    public RMIServerHandler(Client owner,RemoteInterface remoteServer) {
        super(owner,remoteServer);
    }

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
    public RemoteInterface getRemoteServer() {
        return super.getRemoteServer();
    }

    @Override
    public void sendMessageToServer(C2SMessage msg) {
        try {
            getRemoteServer().sendMessageToServer(msg,getOwner().getClient());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
