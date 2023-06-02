package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.ReconnectionAnswer;
import it.polimi.ingsw.Server.Messages.ReconnectionNotifyMsg;
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
        ClientHandler countDownClient= null;
        MyShelfie currentGame = null;


        for(MyShelfie game : games){

            //checking if there is only player while others are disconnected
            if(game.getClientHandlers().stream().filter(x->x.isConnected()).toList().size()==1){
                for(ClientHandler clientHandler: game.getClientHandlers())
                    if(clientHandler.isConnected()){
                        countDownClient = clientHandler;    //the player who is in Countdown Mode
                        currentGame = game;                 //the game which is near to be ended
                    }
            }

            if(game.getDisconnectedClientHandler(nickname, currentClientHandler)){
                canConnect = true;
                break;
            }
        }


        /*if(canConnect){
            games.remove(currentGame);
            currentGame--;
        }*/

        //sending the result of the reconnection choice to the player
        S2CMessage reconnectionAnswer = new ReconnectionAnswer(canConnect);
        currentClientHandler.sendMessageToClient(reconnectionAnswer);

        if(countDownClient!= null && canConnect) {
            //stops the countdown
            countDownClient.sendMessageToClient(new ReconnectionNotifyMsg());
            //allows to play the round to the reconnected player
            currentGame.finishTurn();
        }


        //gestione giocatore con lo stesso nome [...]
    }

    public void getDisconnectedClientHandlerRMI(String nickname, RemoteInterface client){
        //bisogna copiare quello della socket


        //gestione giocatore con lo stesso nome [...]
    }
}