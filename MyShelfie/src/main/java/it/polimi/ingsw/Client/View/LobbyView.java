package it.polimi.ingsw.Client.View;

import java.util.ArrayList;

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
        for(int i = 0; i< lobbyPlayers.size();i++) {
            System.out.format("♦-----------------♦------♦%n");
            System.out.format("|     " + lobbyPlayers.get(i) + "     |     " + (i+1)+ "     |%n");
            System.out.format("♦-----------------♦------♦%n");
        }
        System.out.println("Players currently waiting:" + lobbyPlayers.size());
    }

    @Override
    public void notifyView() {
        synchronized (lock){
            lock.notify();
        }
    }


}
