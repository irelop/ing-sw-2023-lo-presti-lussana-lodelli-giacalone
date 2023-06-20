package it.polimi.ingsw.Client.view.CLI;

import it.polimi.ingsw.Server.Messages.FinishGameRequest;
import it.polimi.ingsw.Server.Messages.LastOneConnectedMsg;

public class LastPlayerConnectedView extends View{

    private LastOneConnectedMsg msg;

    private boolean goOn = true;
    public LastPlayerConnectedView(LastOneConnectedMsg msg){
        this.msg = msg;
    }

    @Override
    public void run(){
        System.out.println("\n"+msg.nickname+", all the other players have disconnected.\n" +
                "At least one of them has to reconnect to this game in 30 seconds or you will be the winner.\n" +
                "\nCOUNTDOWN:\n");
        int i;
        for(i=30; i>=0 && goOn; i--){
            System.out.println(i+" seconds");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //if(i==0) {
        if(goOn) {
            System.out.println("Congratulation " + msg.nickname + "! You are the winner!");
            System.out.println("See you soon " + msg.nickname + "!");
            FinishGameRequest finishGameRequest = new FinishGameRequest();
            getOwner().getServerHandler().sendMessageToServer(finishGameRequest);

            getOwner().setTrueTerminate();
        }

        // }
    }

    public void notifyView(String nickname) {
        this.goOn = false;
        System.out.println(nickname+" reconnected, the game continues! Good luck!");
    }
}
