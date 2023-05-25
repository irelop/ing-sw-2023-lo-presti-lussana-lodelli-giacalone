package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.AllPlayersReadyMsg;
import it.polimi.ingsw.Server.Messages.LobbyUpdateAnswer;

import java.rmi.RemoteException;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

public class LobbyView extends View implements ObserverView {

    private Object lock = new Object();
    private LobbyUpdateAnswer lobbyUpdateAnswer;


    public LobbyView(LobbyUpdateAnswer lobbyUpdateAnswer) {
        this.lobbyUpdateAnswer = lobbyUpdateAnswer;
    }

    @Override
    public void run() {
        synchronized (lock){
            showPlayerTable();
        }
    }

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

        if(lobbyUpdateAnswer.allPlayersReady){
            System.out.println("All players are connected! The game is starting...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ignored) { }

            AllPlayersReadyMsg allPlayersReadyMsg = new AllPlayersReadyMsg();
            if(!getOwner().isRMI())
                getOwner().getServerHandler().sendMessageToServer(allPlayersReadyMsg);
            else {
                try {
                    getOwner().getRemoteServer().sendMessageToServer(allPlayersReadyMsg, getOwner().getClient());
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
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
