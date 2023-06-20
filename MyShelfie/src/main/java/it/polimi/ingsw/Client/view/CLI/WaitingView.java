package it.polimi.ingsw.Client.view.CLI;

import it.polimi.ingsw.Server.chat.ChatStorage;

public class WaitingView extends View implements ObserverView {
    private ChatStorage chat = new ChatStorage();
    private final Object lock = new Object();
    @Override
    public void run() {

        synchronized (lock){
            try {
                String wait = " Please wait...";
                System.out.println(wait);
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
