package it.polimi.ingsw.Server.Model;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.RMIClientHandler;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameRecord {
    private ArrayList<MyShelfie> games;
    private int currentGame;
    private RemoteInterface remoteServer;

    private Object lock;


    public GameRecord() {
        games = new ArrayList<>();
        currentGame = -1;
        lock = new Object();
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


    public void manageLogin(ClientHandler clientHandler, LoginNicknameRequest loginNicknameRequest,MyShelfie controller){
        synchronized (lock) {
            S2CMessage loginNicknameAnswer;

            int controllerIdx = games.indexOf(controller);
            if (games.get(controllerIdx).isStarted()) {
                loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.FULL_LOBBY);
                clientHandler.sendMessageToClient(loginNicknameAnswer);
                return;
            }

            boolean found = false;
            for (MyShelfie game : games) {
                if (!game.checkNickname(loginNicknameRequest.getInsertedNickname())) found = true;
            }
            if (!found) {

                if (games.get(controllerIdx).isFirstConnected()) {
                    loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.FIRST_ACCEPTED);
                    clientHandler.sendMessageToClient(loginNicknameAnswer);
                    games.get(controllerIdx).addPlayer(loginNicknameRequest.getInsertedNickname(), clientHandler);


                } else {
                    loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.ACCEPTED);
                    clientHandler.sendMessageToClient(loginNicknameAnswer);
                    games.get(controllerIdx).addPlayer(loginNicknameRequest.getInsertedNickname(), clientHandler);
                }


            } else {
                loginNicknameAnswer = new LoginNicknameAnswer(loginNicknameRequest, LoginNicknameAnswer.Status.INVALID);
                clientHandler.sendMessageToClient(loginNicknameAnswer);
            }
        }

    }

    public void manageLoginRMI(LoginNicknameRequest msg, RemoteInterface client,MyShelfie controller ){
        synchronized (lock) {
            S2CMessage loginNicknameAnswer;

            int controllerIdx = games.indexOf(controller);
            try {
                if (games.get(controllerIdx).isStarted()) {
                    loginNicknameAnswer = new LoginNicknameAnswer(msg, LoginNicknameAnswer.Status.FULL_LOBBY);
                    client.sendMessageToClient(loginNicknameAnswer);
                    return;
                }

                boolean found = false;
                for (MyShelfie game : games) {
                    if (!game.checkNickname(msg.getInsertedNickname())) found = true;
                }
                if (!found) {

                    if (games.get(controllerIdx).isFirstConnected()) {
                        loginNicknameAnswer = new LoginNicknameAnswer(msg, LoginNicknameAnswer.Status.FIRST_ACCEPTED);
                        client.sendMessageToClient(loginNicknameAnswer);
                        RMIClientHandler clientHandler = new RMIClientHandler(games.get(controllerIdx), client);
                        games.get(controllerIdx).addPlayer(msg.getInsertedNickname(), clientHandler);
                        Thread thread = new Thread(clientHandler);
                        thread.start();
                    } else {
                        loginNicknameAnswer = new LoginNicknameAnswer(msg, LoginNicknameAnswer.Status.ACCEPTED);
                        client.sendMessageToClient(loginNicknameAnswer);
                        RMIClientHandler clientHandler = new RMIClientHandler(games.get(controllerIdx), client);
                        games.get(controllerIdx).addPlayer(msg.getInsertedNickname(), clientHandler);
                        Thread thread = new Thread(clientHandler);
                        thread.start();
                    }


                } else {
                    loginNicknameAnswer = new LoginNicknameAnswer(msg, LoginNicknameAnswer.Status.INVALID);
                    client.sendMessageToClient(loginNicknameAnswer);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }
}
