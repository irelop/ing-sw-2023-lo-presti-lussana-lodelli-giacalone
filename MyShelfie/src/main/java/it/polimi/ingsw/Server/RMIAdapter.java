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

/**
 * RMIAdapter class: this class represents the implementation of the RemoteInterface and allows to expose the methods
 * which can be remotely invoked by client/server during an RMI connection.
 * @authors Irene Lo Presti, Andrea Giacalone
 */

public class RMIAdapter extends UnicastRemoteObject implements RemoteInterface {
    private ArrayList<RemoteInterface> remoteClients;       //moved from public to private
    public HashMap<RemoteInterface, MyShelfie> mapClientsToController;      //mapping for each client their controller
    private Client owner;      //reference to the client if the rmi object is on the client side
    private GameRecord gameRecord;      //reference to the record for multiple games management

    public RMIAdapter() throws RemoteException{
        super();
        this.remoteClients = new ArrayList<>();
        this.mapClientsToController = new HashMap<>();
    }
    //- - - - - - - - - - - - - - - - - - - C O N N E C T I O N   M E T H O D S - - - - - - - - - - - - - - - - - - - -
    public void addRemoteClient(RemoteInterface remoteClient){
        this.remoteClients.add(remoteClient);
    }
    @Override
    public void disconnectRemoteClient(RemoteInterface remoteClient) throws RemoteException {
        this.remoteClients.remove(remoteClient);
    }
    @Override
    public boolean isClientConnected() throws RemoteException {
        if(owner ==null) {
            return false;
        }
        return true;
    }
    public void sendMessageToClient(S2CMessage msg){
        msg.processMessage(null, this);
    }
    public void sendMessageToServer(C2SMessage msg, RemoteInterface remoteClient){
        msg.processMessage(this, remoteClient);
    }

    @Override
    public void ping(){}

    //- - - - - - - - - - - - - - - - - R E M O T E   V I E W   M E T H O D S - - - - - - - - - - - - - - - - - - -
    public void printLoginStatus(String msg){
        System.out.println(msg);
    }
    public void transitionToView(View view){
        owner.transitionToView(view);
    }

    public void goToLoginView(NumberOfPlayerManagementMsg msg){
        transitionToView(new LoginView(msg));
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

    public void notifyView(){
        owner.getCurrentView().notifyView();
    }


    //- - - - - - - - - - - - - - - - - - - - - S E T T E R S - - - - - - - - - - - - - - - - - - - - - -
    public void setOwner(Client owner){
        this.owner = owner;
    }
    public void setGameRecord(GameRecord gameRecord){
        this.gameRecord = gameRecord;
    }
    public void setControllerToClient(RemoteInterface client){
        MyShelfie controller = gameRecord.getGame();
        mapClientsToController.put(client, controller);
    }
    public void resetControllerToClient(MyShelfie controller, RemoteInterface client){
        mapClientsToController.put(client, controller);
    }
    //- - - - - - - - - - - - - - - - - - - - - - G E T T E R S - - - - - - - - - - - - - - - - - - - - -
    public GameRecord getGameRecord(){
        return this.gameRecord;
    }
    public MyShelfie getController(RemoteInterface client){
        return mapClientsToController.get(client);
    }
    public Client getOwner(){
        return owner;
    }

    public View getCurrentView(){
        return owner.getCurrentView();
    }


}