package it.polimi.ingsw.Client.View;

import static java.lang.Thread.sleep;

public class WaitingView extends View implements ObserverView {

    private final Object lock = new Object();
    @Override
    public void run() {
        synchronized (lock) {
            try {
                String wait = " Please wait...";
                System.out.println(wait);
                lock.wait();
            } catch (InterruptedException e) {
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
