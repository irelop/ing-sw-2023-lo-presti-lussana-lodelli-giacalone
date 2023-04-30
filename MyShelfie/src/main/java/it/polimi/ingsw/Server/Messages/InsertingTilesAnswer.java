package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;

public class InsertingTilesAnswer extends S2CMessage {
    public String answer;
    public boolean valid;

    public InsertingTilesAnswer(String answer, boolean valid){
        this.answer = answer;
        this.valid = valid;
    }

    @Override
    public void processMessage(ServerHandler serverHandler){
        serverHandler.getClient().getCurrentView().notifyView();
    }

}
