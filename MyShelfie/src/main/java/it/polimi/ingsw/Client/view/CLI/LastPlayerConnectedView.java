package it.polimi.ingsw.Client.view.CLI;

import it.polimi.ingsw.Server.Messages.FinishGameRequest;
import it.polimi.ingsw.Server.Messages.LastOneConnectedMsg;

/**
 * LastPlayerConnectedView class: this view is shown when there is only one player left connected to the game.
 * A countdown will start and until its end, all the other players, who has disconnected before, have the possibility
 * to rejoin the game, otherwise the player waiting in this view will be declared as the winner.
 *
 * @authors Irene Lo Presti, Andrea Giacalone
 */
public class LastPlayerConnectedView extends View{

    private LastOneConnectedMsg msg;
    private boolean goOn = true;

    /**
     * CONSTRUCTOR: a new LastPlayerConnectedView is built wrapping a message containing the infos about the last player
     * connected to the game.
     * @param msg : message wrapping info of the last player connected.
     */
    public LastPlayerConnectedView(LastOneConnectedMsg msg){
        this.msg = msg;
    }

    @Override
    public void run(){
        System.out.println("\n"+msg.nickname+", all the other players have disconnected.\n" +
                "At least one of them has to reconnect to this game in 30 seconds or you will be the winner.\n" +
                "\nCOUNTDOWN:\n");
        int i;
        //starting the countdown
        for(i=30; i>=0 && goOn; i--){
            System.out.println(i+" seconds");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //the countdown wasn't notified by any event of reconnection: the last player connected wins the game
        if(goOn) {
            System.out.println("Congratulation " + msg.nickname + "! You are the winner!");
            System.out.println("See you soon " + msg.nickname + "!");

            //building the message to finish the game
            FinishGameRequest finishGameRequest = new FinishGameRequest();
            getOwner().getServerHandler().sendMessageToServer(finishGameRequest);

            getOwner().setTrueTerminate();  //stopping the view state machine
        }

    }

    /**
     * OVERVIEW: this method allows to notify the player in countdown mode about the reconnection of at least one other
     * player previously disconnected to the game.
     * @param nickname: the nickname of the player now reconnected to the game
     */
    public void notifyView(String nickname) {
        this.goOn = false;
        System.out.println(nickname+" reconnected, the game continues! Good luck!");
    }
}
