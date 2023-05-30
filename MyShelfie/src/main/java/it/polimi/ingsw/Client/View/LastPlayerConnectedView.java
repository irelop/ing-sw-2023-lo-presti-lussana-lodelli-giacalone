package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.FinishGameRequest;
import it.polimi.ingsw.Server.Messages.LastOneConnectedMsg;

import java.rmi.RemoteException;

public class LastPlayerConnectedView extends View{

    private LastOneConnectedMsg msg;

    public LastPlayerConnectedView(LastOneConnectedMsg msg){
        this.msg = msg;
    }

    @Override
    public void run(){
        System.out.println("\n"+msg.nickname+", all the other players have disconnected.\n" +
                "At least one of them have to reconnect to this game in 20 seconds or you will be the winner.\n" +
                "COUNTDOWN:\n");
        int i;
        for(i=20; i>=0; i--){
            System.out.println(i+" seconds");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //if(i==0) {
            System.out.println("Congratulation " + msg.nickname + "! You are the winner!");
            System.out.println("See you soon " + msg.nickname + "!");
            getOwner().setTrueTerminate();
            FinishGameRequest finishGameRequest = new FinishGameRequest();
            if (!getOwner().isRMI()) {
                getOwner().getServerHandler().sendMessageToServer(finishGameRequest);
            }
            else {
                try {
                    getOwner().getRemoteServer().sendMessageToServer(finishGameRequest);
                } catch (RemoteException e) {
                throw new RuntimeException(e);
                }
        }

        // }
    }

}
