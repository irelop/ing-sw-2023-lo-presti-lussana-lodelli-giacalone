package it.polimi.ingsw.Client.Gui;

import it.polimi.ingsw.Server.Messages.FinishGameRequest;

public class Countdown implements Runnable {

    public boolean goOn = false;

    public LastPlayerConnectedController controller;

    @Override
    public void run() {
        for(int i=30; i>=0 && goOn; i--){
            System.out.println(i+" seconds");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(goOn){
            controller.alertText.setText("Congratulation! You are the winner!");
            FinishGameRequest finishGameRequest = new FinishGameRequest();
            controller.getOwner().getServerHandler().sendMessageToServer(finishGameRequest);
        }
    }
}
