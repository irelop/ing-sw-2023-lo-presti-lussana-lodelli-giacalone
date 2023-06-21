package it.polimi.ingsw.Client.view.CLI;

import it.polimi.ingsw.Server.Messages.AllPlayersReadyMsg;
import it.polimi.ingsw.Server.Messages.LobbyUpdateAnswer;

import java.util.Formatter;
import java.util.concurrent.TimeUnit;
/**
 * LobbyView class: this view shows the lobby in which players are redirected before the start of the game. The lobby
 * updates every time a new player is connected to the game.
 * @author Andrea Giacalone
 */
public class LobbyView extends View implements ObserverView {

    private Object lock = new Object();
    private LobbyUpdateAnswer lobbyUpdateAnswer;    //the updated view of the lobby sent by the server

    /**
     * CONSTRUCTOR: a new LobbyView object is built receiving the updated lobby view containing the players currently
     * connected to this game and waiting for the start.
     * @param lobbyUpdateAnswer: the lobby of players currently connected.
     */
    public LobbyView(LobbyUpdateAnswer lobbyUpdateAnswer) {
        this.lobbyUpdateAnswer = lobbyUpdateAnswer;
    }

    @Override
    public void run() {
        synchronized (lock){
            showPlayerTable();
            checkPlayersReady();
        }
    }

    /**
     * OVERVIEW: a printing method showing the lobby of connected players as a formatted table which shows their nicknames
     * and their order of connection to the game.
     */
    public void showPlayerTable(){
        Formatter fmt = new Formatter();

        fmt.format("%s %15s %15s %s\n","╔","","","╗");
        fmt.format("%15s %15s\n","NICKNAME","ID");
        fmt.format("  ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬  \n","","");
        for(int i = 0; i<lobbyUpdateAnswer.lobbyPlayers.size();i++){
            fmt.format("%15s %15s\n",lobbyUpdateAnswer.lobbyPlayers.get(i),(i+1));
        }
        fmt.format("%s %15s %15s %s\n","╚","","","╝");
        System.out.println(fmt);
    }


    /**
     * OVERVIEW: this method allows to notify players if, once reached the amount of players set for the lobby,
     * everything is ready to start the game.
     */
    private void checkPlayersReady(){

        if(lobbyUpdateAnswer.allPlayersReady){
            System.out.println("All players are connected! The game is starting...");
            try {
                TimeUnit.SECONDS.sleep(2); //simulating computation
            } catch (InterruptedException ignored) { }

            AllPlayersReadyMsg allPlayersReadyMsg = new AllPlayersReadyMsg();
            getOwner().getServerHandler().sendMessageToServer(allPlayersReadyMsg);
        }
    }

    @Override
    public void notifyView() {
        synchronized (lock){
            lock.notify();
        }
    }

}
