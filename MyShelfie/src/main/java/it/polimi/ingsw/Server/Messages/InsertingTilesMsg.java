package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.ClientHandler;

public class InsertingTilesMsg extends C2SMessage {

    private int columnChosen;
    private int[] chosenOrderIndexes;

    public InsertingTilesMsg(int columnChosen, int[] chosenOrderIndexes) {
        this.columnChosen = columnChosen;
        this.chosenOrderIndexes = chosenOrderIndexes;
    }

    @Override
    public void processMessage(ClientHandler clientHandler){
        clientHandler.sendMessageToServer(this);
    }
}
