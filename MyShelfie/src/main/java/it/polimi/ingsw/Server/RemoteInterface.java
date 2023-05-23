package it.polimi.ingsw.Server;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.View;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.Model.MyShelfie;

import java.rmi.Remote;
import java.rmi.RemoteException;



public interface RemoteInterface extends Remote {
        void addRemoteClient(it.polimi.ingsw.Server.RemoteInterface client) throws RemoteException;
        //void addController(MyShelfie controller) throws RemoteException;
        //void setMapClientsToController(MyShelfie controller) throws RemoteException;
        // MyShelfie getController(RemoteInterface client) throws RemoteException;
        void sendMessageToServer(C2SMessage msg) throws RemoteException;
        void setController(MyShelfie controller) throws RemoteException;
        MyShelfie getController() throws RemoteException;
        void printLoginStatus(String msg) throws  RemoteException;
        RemoteInterface getRemoteClient(int index) throws RemoteException;
        void sendMessageToClient(S2CMessage msg) throws  RemoteException;
        void setClient(Client client) throws RemoteException;
        void transitionToView(View view) throws RemoteException;

        void goToLobbyView(LobbyUpdateAnswer msg) throws RemoteException;
        void goToChooseTilesFromBoardView(YourTurnMsg msg) throws RemoteException;
        void sendMessageToServer(C2SMessage msg, it.polimi.ingsw.Server.RemoteInterface client) throws RemoteException;
        View getCurrentView() throws RemoteException;
        void notifyView() throws RemoteException;
        void goToInsertInShelfView(ToShelfMsg msg) throws RemoteException;
        void goToGoalView(GoalAndScoreMsg msg) throws RemoteException;

        void goToGameIsEndingView(GameIsEndingUpdateAnswer msg) throws RemoteException;
        void goToEndgameView(ScoreBoardMsg msg) throws RemoteException;
        Client getClient() throws RemoteException;

}