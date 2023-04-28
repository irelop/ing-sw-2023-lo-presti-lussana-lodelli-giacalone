package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;

public class ExceptionMsg extends S2CMessage{
    public Exception exceptionToBeForwarded;

    public ExceptionMsg(Exception exceptionToBeForwarded){
        this.exceptionToBeForwarded = exceptionToBeForwarded;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        this.exceptionToBeForwarded.getMessage();
    }
}
