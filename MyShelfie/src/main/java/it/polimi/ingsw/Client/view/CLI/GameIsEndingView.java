package it.polimi.ingsw.Client.view.CLI;

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

            if(msg.firstToFinish == msg.playerIndex)
                System.out.println("\nCongratulation, you are the first one to fill the personal shelf!");
            else
                System.out.println("\n"+msg.players[msg.firstToFinish]+" is the first one to fill the personal shelf.");

            if(!msg.gameOver) {
                System.out.println("The game is ending, the other players are playing their last turn.");
                for (int i = 0; i < msg.players.length; i++) {
                    if (i != msg.playerIndex) {
                        if (msg.hasFinished[i])
                            System.out.println(msg.players[i] + " has finished their turn.");
                        else
                            System.out.println(msg.players[i] + " hasn't played their turn yet.");
                    }
                }
            }

            //se la partita è finita devo andare nella view con la classifica
            else{
                //playerIndex needed to choose the right clientHandler
                System.out.println("Everyone has played their turn!");
                EndGameMsg endGameMsg = new EndGameMsg(msg.playerIndex);
                if(!getOwner().isRMI())
                    getOwner().getServerHandler().sendMessageToServer(endGameMsg);
                else{
                    getOwner().getServerHandler().sendMessageToServer(endGameMsg);
                }
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
