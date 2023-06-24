package it.polimi.ingsw.Client;

import it.polimi.ingsw.Server.Messages.C2SMessage;

/**
 * ServerHandler class: an abstraction of the network handler used by the client in order to send and process messages
 *                      to/from the server
 */

public abstract class ServerHandler implements Runnable{
    protected final Client owner; //the client associated to this handler: it is unique for this class

    public ServerHandler(Client owner) {
        this.owner = owner;
    }



    /**
     * OVERVIEW: this method allows to send a message from the client to the server independently of the
     *           network technology chosen.
     * @param msg : message to be forwarded to the server.
     */
    public void sendMessageToServer(C2SMessage msg){}


    /**
     * OVERVIEW: this method allows to close safely the handler or in order to manage possible disconnection.
     */
    public void stop(){}

    //- - - - - - - G E T T E R S- - - - - - - - - -

    public Client getOwner(){
        return owner;       //the client associated to this handler
    }
}
