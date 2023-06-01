package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.ReconnectionAnswer;
import it.polimi.ingsw.Server.Messages.ReconnectionRequest;
import it.polimi.ingsw.Server.Messages.S2CMessage;

import java.util.ArrayList;

public class GameRecord {
    private ArrayList<MyShelfie> games;
    private int currentGame;


    public GameRecord() {
        games = new ArrayList<>();
        currentGame = -1;
    }

    public MyShelfie getGame(){
        if (currentGame == -1 || games.get(currentGame).getAllPlayersReady()) {
            MyShelfie game = new MyShelfie(this);
            games.add(game);
            currentGame++;
        }
        return games.get(currentGame);
    }

    public void getDisconnectedClientHandler(String nickname){
        ClientHandler clientHandler = null;
        for(MyShelfie game:games){
            clientHandler = game.getDisconnectedClientHandler(nickname);
        }

        S2CMessage reconnectionAnswer = new ReconnectionAnswer(clientHandler);

        //gestione giocatore con lo stesso nome [...]
    }
}
