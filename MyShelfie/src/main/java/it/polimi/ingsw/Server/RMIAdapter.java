package it.polimi.ingsw.Server;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.View.*;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.MyShelfie;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMIAdapter extends UnicastRemoteObject implements RemoteInterface {
    public ArrayList<RemoteInterface> remoteClients;
    /*gestione partite multiple
        public ArrayList<MyShelfie> controllers;
        public HashMap<RemoteInterface, MyShelfie> mapClientsToController;
     */
    private MyShelfie controller;
    private Client client;

    public RMIAdapter() throws RemoteException{
        super();
        this.remoteClients = new ArrayList<>();
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

    public MyShelfie getController(){
        return controller;
    }

    public void sendMessageToServer(C2SMessage msg){
        //msg.processMessage(this);
    }
    public void sendMessageToClient(S2CMessage msg){
        //msg.processMessage(this);
    }
    public void sendMessageToServer(C2SMessage msg, RemoteInterface remoteClient){
        //msg.processMessage(this, remoteClient);
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
        transitionToView(new EndgameView(msg));
    }

    public Client getClient(){
        return client;
    }
}
