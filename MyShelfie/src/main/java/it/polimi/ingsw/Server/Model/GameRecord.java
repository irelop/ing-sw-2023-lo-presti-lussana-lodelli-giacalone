package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.ReconnectionAnswer;
import it.polimi.ingsw.Server.Messages.ReconnectionRequest;
import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;
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
            MyShelfie game = new MyShelfie();
            games.add(game);
            currentGame++;
        }
        return games.get(currentGame);
    }

    public void getDisconnectedClientHandlerSocket(String nickname, ClientHandler currentClientHandler){
        boolean canConnect = false;
        for(MyShelfie game : games){
            if(game.getDisconnectedClientHandler(nickname, currentClientHandler)){
                canConnect = true;
                break;
            }
        }
        /*if(canConnect){
            games.remove(currentGame);
            currentGame--;
        }*/
        S2CMessage reconnectionAnswer = new ReconnectionAnswer(canConnect);
        currentClientHandler.sendMessageToClient(reconnectionAnswer);


        //gestione giocatore con lo stesso nome [...]
    }

    public void getDisconnectedClientHandlerRMI(String nickname, RemoteInterface client){
        //bisogna copiare quello della socket


        //gestione giocatore con lo stesso nome [...]
    }
}
