package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.EndGameMsg;
import it.polimi.ingsw.Server.Messages.GameIsEndingUpdateAnswer;

public class GameIsEndingView extends View{

    private final Object lock;
    private GameIsEndingUpdateAnswer msg;

    public GameIsEndingView(GameIsEndingUpdateAnswer msg){
        lock = new Object();
        this.msg = msg;
    }

    @Override
    public void run() {
        synchronized (lock){
            System.out.println("The game is ending, the other players are playing their last turn.");
            //se la partita è finita devo andare nella view con la classifica

            if(msg.gameOver){
                //playerIndex needed to choose the right clientHandler
                EndGameMsg endGameMsg = new EndGameMsg(msg.playerIndex);
                getOwner().getServerHandler().sendMessageToServer(endGameMsg);
            }
        }
    }

    @Override
    public void notifyView() {
        synchronized (lock){
            lock.notify();
        }
    }
}
