package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Client.Client;

/*I need to implement View as a runnable because I need to create a thread for each view in order to synchronize with
  other clients and in general I need the view to be responsive when it has to be and locked where it cannot accept any
  valid input.
 */
public abstract class View implements Runnable, ObserverView {
    private Client owner;
    /* a boolean used for managing interactions with other thread: if false, it locks the view and no other can interact
       with it, otherwise when it will be true, this thread will be free to open new possible interactions.
     */

    private boolean stopInteraction;

    /**
     * this setter method allows to establish the client owner of that view.
     * @param owner the client owner of the view.
     */
    public void setOwner(Client owner){
        this.owner = owner;
    }

    /**
     * this getter method allows to get the client owner of that view.
     * @return owner the client owner of the view.
     */
    public Client getOwner(){
        return this.owner;
    }

    /**
     * main function of the class: it wraps user interactions, wrapping messages and invoking correspondents methods
     * in the server handler in order to send in the network.
     */
    @Override
    public void run() {}

    /**
     * a getter of the lock flag associated to this view.
     * @return stopInteraction: lock flag
     */
    synchronized protected boolean getStopInteraction(){
        return stopInteraction;
    }

    /**
     * setter method which switch the stopInteraction to true in order to inform the run method() to terminate as soon
     * as possible ( metodo alternativo alla stop() che potrebbe generare casini).
     */
    public void setStopInteraction(){
        stopInteraction = true;
        //all waiting threads are awaken and the first will get lock, setting the flag again to false in the run method.
        //notifyAll();
    }

    @Override
    public void notifyView() {
    }
}
