package it.polimi.ingsw.Client.Gui;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Server.Messages.S2CMessage;

/**
 * This abstract class manages the link between the Client and the current FXML file controller
 * It also gives some common methods that will be implemented in the different controllers
 */
public abstract class Controller {
    private Client owner;

    /**
     * Allows the controller to be built with an input message passed as a
     * parameter, in order to set some values like the current player shelf exc.
     * @param message: the server message containing values
     */
    public abstract void build(S2CMessage message);

    /**
     * Allows the controller to receive a message during its execution,
     * in order to call some methods that manage server answers
     * @param message: the server message (usually an xxxAnswerMsg)
     */
    public abstract void receiveAnswer(S2CMessage message);

    public Client getOwner() {
        return owner;
    }
    public void setOwner(Client owner) {
        this.owner = owner;
    }
}
