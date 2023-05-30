package it.polimi.ingsw.Server;

import it.polimi.ingsw.Server.Model.MyShelfie;

import java.rmi.RemoteException;

public class RMIClientHandler extends ClientHandler {

    public RMIClientHandler(MyShelfie game, RemoteInterface remoteClient){
        super(game, remoteClient);
        setIsRMI(true);
    }

    @Override
    public void run(){
        boolean goOn = true;
        while(goOn){
            try{
                Thread.sleep(5000);
                getClientInterface().ping();
            }catch(Exception e){
                System.out.println("RMI client disconnected");
                getController().shouldFinishTurn(this);
                goOn = false;
            }
        }
    }
}
