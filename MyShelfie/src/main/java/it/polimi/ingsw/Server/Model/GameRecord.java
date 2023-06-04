package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.ReconnectionAnswer;
import it.polimi.ingsw.Server.Messages.ReconnectionNotifyMsg;
import it.polimi.ingsw.Server.Messages.S2CMessage;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameRecord {
    private ArrayList<MyShelfie> games;
    private int currentGame;
    private RemoteInterface remoteServer;


    public GameRecord() {
        games = new ArrayList<>();
        currentGame = -1;
    }

    public void setRemoteServer(RemoteInterface remoteServer){
        this.remoteServer = remoteServer;
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
        int playerIndex = -1;


        for(MyShelfie game : games){

            playerIndex = game.checkPlayerDisconnected(nickname);

            if(playerIndex != -1){

                //checking if there is only player while others are disconnected
                if(game.getClientHandlers().stream().filter(ClientHandler::isConnected).toList().size()==1){
                    for(ClientHandler clientHandler: game.getClientHandlers())
                        if(clientHandler.isConnected()){
                            countDownClient = clientHandler;    //the player who is in Countdown Mode
                            currentGame = game;                 //the game which is near to be ended
                        }
                }

                game.switchSocketClientHandler(playerIndex, currentClientHandler);
                canConnect = true;
                break;
            }
        }

        //sending the result of the reconnection choice to the player
        S2CMessage reconnectionAnswer = new ReconnectionAnswer(canConnect);
        currentClientHandler.sendMessageToClient(reconnectionAnswer);

        if(countDownClient != null) {
            //stops the countdown
            if(!countDownClient.getIsRMI())
                countDownClient.sendMessageToClient(new ReconnectionNotifyMsg(nickname));
            else{
                try {
                    countDownClient.getClientInterface().sendMessageToClient(new ReconnectionNotifyMsg(nickname));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            //allows to play the round to the reconnected player
            currentGame.finishTurn();
        }


        //gestione giocatore con lo stesso nome [...]
    }

    public void getDisconnectedClientHandlerRMI(String nickname, RemoteInterface client){
        boolean canConnect = false;
        ClientHandler countDownClient= null;
        MyShelfie currentGame = null;
        int playerIndex = -1;


        for(MyShelfie game : games){

            playerIndex = game.checkPlayerDisconnected(nickname);
            if(playerIndex != -1){

                //checking if there is only player while others are disconnected
                if(game.getClientHandlers().stream().filter(ClientHandler::isConnected).toList().size()==1){
                    for(ClientHandler clientHandler: game.getClientHandlers())
                        if(clientHandler.isConnected()){
                            countDownClient = clientHandler;    //the player who is in Countdown Mode
                            currentGame = game;                 //the game which is near to be ended
                        }
                }
                game.switchRMIClientHandler(playerIndex, client);
                try {
                    remoteServer.setMapClientsToController(game, client);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                canConnect = true;
                break;

            }
        }

        //sending the result of the reconnection choice to the player
        S2CMessage reconnectionAnswer = new ReconnectionAnswer(canConnect);
        try {
            client.sendMessageToClient(reconnectionAnswer);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        if(countDownClient!= null) {
            //stops the countdown
            if(!countDownClient.getIsRMI())
                countDownClient.sendMessageToClient(new ReconnectionNotifyMsg(nickname));
            else{
                try {
                    countDownClient.getClientInterface().sendMessageToClient(new ReconnectionNotifyMsg(nickname));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
            //allows to play the round to the reconnected player
            currentGame.finishTurn();
        }


        //gestione giocatore con lo stesso nome [...]
    }
}
