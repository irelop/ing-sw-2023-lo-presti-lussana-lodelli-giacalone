package it.polimi.ingsw.Client.view.CLI;

/**
 * ObserverView interface: this interface allows to call its main method of notify for views which needs to be updated
 *                           after an answer from the server.
 *                           It allows to implement the Observer-Observable pattern.
 *
 * @author Andrea Giacalone
 */
public interface ObserverView {
    /**
     * OVERVIEW: this method allows to notify the view triggering a different action according to its implementation.
     * @author Andrea Giacalone
     */
     void notifyView();

}
