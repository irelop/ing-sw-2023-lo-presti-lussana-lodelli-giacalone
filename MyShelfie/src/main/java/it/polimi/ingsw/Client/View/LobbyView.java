package it.polimi.ingsw.Client.View;

import java.util.ArrayList;
import java.util.Formatter;

public class LobbyView extends View implements ObserverView {

    private ArrayList<String> lobbyPlayers;
    private Object lock = new Object();

    public LobbyView(ArrayList<String> lobbyPlayers) {
        this.lobbyPlayers = lobbyPlayers;
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
        for(int i = 0; i<lobbyPlayers.size();i++){
            fmt.format("%15s %15s\n",lobbyPlayers.get(i),(i+1));
        }
        fmt.format("%s %15s %15s %s\n","╚","","","╝");
        System.out.println(fmt);



        /* Alternative option
        for(int i = 0; i< lobbyPlayers.size();i++) {
            System.out.printf("╔▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬╗%n");
            System.out.printf("║    %-6s - ID: %-11d║%n",lobbyPlayers.get(i),(i+1));
            System.out.printf("╚▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬╝%n");
        }
        System.out.println("Players currently waiting:" + lobbyPlayers.size());
         */
    }

    @Override
    public void notifyView() {
        synchronized (lock){
            lock.notify();
        }
    }


}
