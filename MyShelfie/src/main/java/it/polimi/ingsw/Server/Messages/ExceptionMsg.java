package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ExceptionView;

public class ExceptionMsg extends S2CMessage{
    public String exceptionString;

    public ExceptionMsg(String exceptionString){
        this.exceptionString = exceptionString;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().transitionToView(new ExceptionView(this));
    }
}
