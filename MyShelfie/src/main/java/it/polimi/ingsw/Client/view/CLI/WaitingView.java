package it.polimi.ingsw.Client.view.CLI;

/**
 * WaitingView class: an idle view used to notify the player that notifies the player his/her waiting status while
 * other players are playing their round.
 * @author Matteo Lussana
 */
public class WaitingView extends View implements ObserverView {
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
