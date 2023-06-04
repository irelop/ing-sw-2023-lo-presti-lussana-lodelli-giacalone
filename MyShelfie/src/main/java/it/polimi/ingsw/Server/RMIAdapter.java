package it.polimi.ingsw.Server;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.View.*;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.GameRecord;
import it.polimi.ingsw.Server.Model.MyShelfie;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class RMIAdapter extends UnicastRemoteObject implements RemoteInterface {
    public ArrayList<RemoteInterface> remoteClients;

    /*gestione partite multiple*/
    public ArrayList<MyShelfie> controllers;
    public HashMap<RemoteInterface, MyShelfie> mapClientsToController;

    private MyShelfie controller;
    private Client client;
    private GameRecord gameRecord;

    public RMIAdapter() throws RemoteException{
        super();
        this.remoteClients = new ArrayList<>();
        this.controllers = new ArrayList<>();
        this.mapClientsToController = new HashMap<>();
    }

    public void addRemoteClient(RemoteInterface remoteClient){
        this.remoteClients.add(remoteClient);
    }

    public void setClient(Client client){
        this.client = client;
    }

    //da sistemare per più rmi clients
    public RemoteInterface getRemoteClient(int index){
        return this.remoteClients.get(index);
    }

    public void setController(MyShelfie controller){
        this.controller = controller;
    }
    public void addController(MyShelfie controller){
        this.controllers.add(controller);
    }

    public void setMapClientsToController(MyShelfie controller, RemoteInterface client){
        mapClientsToController.put(client, controller);
    }
    public void setMapClientsToController(RemoteInterface client){
        MyShelfie controller = gameRecord.getGame();
        mapClientsToController.put(client, controller);
    }

    @Override
    public boolean isClientConnected() throws RemoteException {
        if(client==null) {
            return false;
        }
        return true;
    }

    public MyShelfie getController(RemoteInterface client){

        /*if(mapClientsToController.get(client)==null){
            //I need to add an entry for the reconnected client and the retrieved current game
            mapClientsToController.put(client,gameRecord.getCurrentGame());
        }*/
        return mapClientsToController.get(client);

    }

    public void sendMessageToServer(C2SMessage msg){
        msg.processMessage(this, null);
    }
    public void sendMessageToClient(S2CMessage msg){
        msg.processMessage(null, this);
    }
    public void sendMessageToServer(C2SMessage msg, RemoteInterface remoteClient){
        msg.processMessage(this, remoteClient);
    }
    public void printLoginStatus(String msg){
        System.out.println(msg);
    }
    public void transitionToView(View view){
        client.transitionToView(view);
    }
    public View getCurrentView(){
        return client.getCurrentView();
    }
    public void notifyView(){
        client.getCurrentView().notifyView();
    }
    public void goToChooseTilesFromBoardView(YourTurnMsg msg){
        transitionToView(new ChooseTilesFromBoardView(msg));
    }
    public void goToInsertInShelfView(ToShelfMsg msg){
        transitionToView(new InsertInShelfView(msg));
    }
    public void goToGoalView(GoalAndScoreMsg msg){
        transitionToView(new GoalView(msg));
    }
    public void goToLobbyView(LobbyUpdateAnswer msg){
        transitionToView(new LobbyView(msg));
    }
    public void goToGameIsEndingView(GameIsEndingUpdateAnswer msg){
        transitionToView(new GameIsEndingView(msg));
    }
    public void goToEndgameView(ScoreBoardMsg msg){
        transitionToView(new EndGameView(msg));
    }
    public void goToLastPlayerConnectedView(LastOneConnectedMsg msg){
        transitionToView(new LastPlayerConnectedView(msg));
    }

    public Client getClient(){
        return client;
    }
    public int getNumClients(){
        return remoteClients.size();
    }

    @Override
    public void disconnectClient(RemoteInterface remoteClient) throws RemoteException {
        this.remoteClients.remove(remoteClient);
    }

    public void ping(){
        client.ping();
    }

    public void setGameRecord(GameRecord gameRecord){
        this.gameRecord = gameRecord;
    }
    public GameRecord getGameRecord(){
        return this.gameRecord;
    }
}
