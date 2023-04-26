package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;

public class LoginNumPlayersRequest extends C2SMessage{
    private int insertedNumPlayers;

    public LoginNumPlayersRequest(int insertedNumPlayers) {
        this.insertedNumPlayers = insertedNumPlayers;
    }

    


    @Override
    public void processMessage(ClientHandler clientHandler) {

        clientHandler.getController().setNumberOfPlayers(insertedNumPlayers);

    }
}
