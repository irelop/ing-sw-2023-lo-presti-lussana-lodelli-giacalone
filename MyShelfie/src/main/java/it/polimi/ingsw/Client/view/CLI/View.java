package it.polimi.ingsw.Client.view.CLI;

import it.polimi.ingsw.Client.Client;


public abstract class View implements Runnable, ObserverView {
    private Client owner;
    private boolean stopInteraction;


    /**
     * OVERVIEW: this method represents the core of the class and it wraps user interactions, wrapping messages and
     * invoking correspondents methods in the server handler in order to send in the network.
     */
    @Override
    public void run() {}


    @Override
    public void notifyView() {
    }

    // - - - - - - - - - - - - - - - S E T T E R S - - - - - - - - - - - - - - - - -  -

    /**
     * OVERVIEW: this setter method allows to establish the client owner of that view.
     * @param owner the client owner of the view.
     */
    public void setOwner(Client owner){
        this.owner = owner;
    }

    /**
     * OVERVIEW: setter method which notifies the view execution to terminate as soon as possible.
     */
    public void setStopInteraction(){
        stopInteraction = true;

    }

    // - - - - - - - - - - - - - - - G E T T E R S - - - - - - - - - - - - - - - - -  -

    /**
     * OVERVIEW: this getter method allows to get the client owner of that view.
     * @return owner the client owner of the view.
     */
    public Client getOwner(){
        return this.owner;
    }

}
