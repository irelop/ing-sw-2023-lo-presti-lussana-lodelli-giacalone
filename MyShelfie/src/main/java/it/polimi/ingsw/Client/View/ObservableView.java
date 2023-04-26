package it.polimi.ingsw.Client.View;

/**
 * ObservableView interface: this interface allows to calls its main method of notify for views which needs to be updated
 *                           after an answer from the server.
 *                           It allows to implement the Observer-Observable patttern.
 *
 * @author Andrea Giacalone
 */
public interface ObservableView {
    public void notifyView();

}
