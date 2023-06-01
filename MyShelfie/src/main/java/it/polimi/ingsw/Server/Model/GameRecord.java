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
            MyShelfie game = new MyShelfie(this);
            games.add(game);
            currentGame++;
        }
        return games.get(currentGame);
    }

    public void getDisconnectedClientHandlerSocket(String nickname, ClientHandler currentClientHandler){
        ClientHandler clientHandler = null;
        /*for(int i=0; i<games.size()-1; i++){
            clientHandler = games.get(i).getDisconnectedClientHandler(nickname);
            if(clientHandler != null)
                break;
        }
        boolean canConnect;
        if(clientHandler == null)
           canConnect = false;
        else canConnect = true;
        S2CMessage reconnectionAnswer = new ReconnectionAnswer(canConnect);
        currentClientHandler.sendMessageToClient(reconnectionAnswer);

        if(clientHandler!=null){
            /*clientHandler.setIsConnected(true);
            Thread thread = new Thread(clientHandler);
            thread.start();

        }*/
        boolean canConnect = false;
        for(int i=0; i<games.size()-1; i++){
            if(games.get(i).getDisconnectedClientHandler(nickname, currentClientHandler)){
                canConnect = true;
                break;
            }
        }
        if(canConnect){
            games.remove(currentGame);
            currentGame--;
        }
        S2CMessage reconnectionAnswer = new ReconnectionAnswer(canConnect);
        currentClientHandler.sendMessageToClient(reconnectionAnswer);


        //gestione giocatore con lo stesso nome [...]
    }

    public void getDisconnectedClientHandlerRMI(String nickname, RemoteInterface client){
        ClientHandler clientHandler = null;
        for(int i=0; i<games.size()-1; i++){
            clientHandler = games.get(i).getDisconnectedClientHandler(nickname);
            if(clientHandler != null)
                break;
        }

        boolean canConnect;
        if(clientHandler == null)
            canConnect = false;
        else canConnect = true;
        S2CMessage reconnectionAnswer = new ReconnectionAnswer(canConnect);
        try {
            client.sendMessageToClient(reconnectionAnswer);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        //gestione giocatore con lo stesso nome [...]
    }
}
