package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;

public class InitialPositionAnswer extends S2CMessage{

    public String answer;
    public InitialPositionMsg parent;

    public InitialPositionAnswer(String answer, InitialPositionMsg parent){
        this.answer = answer;
        this.parent = parent;
    }

    public InitialPositionMsg getParent(){
        return parent;
    }

    public String getAnswer(){
        return answer;
    }

    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getClient().getCurrentView().notifyView();
    }

}
