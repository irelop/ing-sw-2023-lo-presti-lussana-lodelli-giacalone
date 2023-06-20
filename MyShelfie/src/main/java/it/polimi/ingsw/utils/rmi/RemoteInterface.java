package it.polimi.ingsw.utils.rmi;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Client.view.CLI.View;
import it.polimi.ingsw.Server.Messages.*;
import it.polimi.ingsw.Server.controller.GameRecord;
import it.polimi.ingsw.Server.controller.MyShelfie;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RemoteInterface: this interface allows to expose methods which can be remotely invoked bu actors who has set up
 * an RMI communication.
 * @authors Irene Lo Presti, Andrea Giacalone
 */

public interface RemoteInterface extends Remote {
        //- - - - - - - - - - - - - - - - - - - C O N N E C T I O N   M E T H O D S - - - - - - - - - - - - - - - - - -

        /**
         * OVERVIEW: this method allows to add a remote client to the remote server.
         * @param remoteClient: the remote client to be added.
         * @throws RemoteException: if an error occurs during the remote invocation.
         */
        void addRemoteClient(RemoteInterface remoteClient) throws RemoteException;

        /**
         * OVERVIEW: this method allows to manage the remove a remote client from the remote server when a
         * disconnection occurs.
         * @param remoteClient: the remote client to be removed.
         * @throws RemoteException: if an error occurs during the remote invocation.
         */
        void disconnectRemoteClient(RemoteInterface remoteClient)throws RemoteException;


        /**
         * OVERVIEW: this method allows to check if the client is still connected or not.
         * @return true: if it's still connected, false otherwise.
         * @throws RemoteException: if an error occurs during remote invocation.
         */
        boolean isClientConnected() throws RemoteException;

        /**
         * OVERVIEW: this method allows to send a message from the remote client to the remote server during
         * an RMI communication.
         * @param msg: the message to be processed in the remote server.
         * @param remoteClient: the sender of the message.
         * @throws RemoteException: if an error occurs in sending the message.
         */
        void sendMessageToServer(C2SMessage msg, RemoteInterface remoteClient) throws RemoteException;

        /**
         * OVERVIEW: this method allows to send a message from the remote server to the remote client during an RMI
         * communication.
         * @param msg: the message to be processed in the remote client.
         * @throws RemoteException: if an error occurs in sending the message.
         */
        void sendMessageToClient(S2CMessage msg) throws  RemoteException;

        /**
         * OVERVIEW: a facade method whose only purpose is to check if the other network actor is still active or not.
         * @throws RemoteException: if the other actor is no longer active.
         */
        void ping() throws RemoteException;

        //- - - - - - - - - - - - - - - - - R E M O T E   V I E W   M E T H O D S - - - - - - - - - - - - - - - - - - -
        /**
         * OVERVIEW: this method allows to print status messages on the other side of the network.
         * @param msg: the status message to be printed.
         * @throws RemoteException: if an error occurs during remote invocation.
         */
        void printLoginStatus(String msg) throws  RemoteException;

        /**
         * OVERVIEW: this method allows to make a state machine transition to the targeted view.
         * @param view: the next view to be executed.
         * @throws RemoteException: if an error occurs during remote invocation.
         */
        void transitionToView(View view) throws RemoteException;
        void goToLoginView(NumberOfPlayerManagementMsg msg) throws RemoteException;
        void goToLobbyView(LobbyUpdateAnswer msg) throws RemoteException;
        void goToChooseTilesFromBoardView(YourTurnMsg msg) throws RemoteException;
        void goToInsertInShelfView(ToShelfMsg msg) throws RemoteException;
        void goToGoalView(GoalAndScoreMsg msg) throws RemoteException;
        void goToGameIsEndingView(GameIsEndingUpdateAnswer msg) throws RemoteException;
        void goToEndgameView(ScoreBoardMsg msg) throws RemoteException;
        void goToLastPlayerConnectedView(LastOneConnectedMsg msg) throws RemoteException;

        /**
         * OVERVIEW: this method allows to remotely the current view.
         * @throws RemoteException: if an error occurs during remote invocation.
         */
        void notifyView() throws RemoteException;



        //- - - - - - - - - - - - - - - - - - - - - S E T T E R S - - - - - - - - - - - - - - - - - - - - - -

        void setGameRecord(GameRecord gameRecord) throws RemoteException;
        void setOwner(Client owner) throws RemoteException;
        void setControllerToClient(RemoteInterface client) throws RemoteException;
        void resetControllerToClient(MyShelfie controller, RemoteInterface client) throws RemoteException;

        //- - - - - - - - - - - - - - - - - - - - - - G E T T E R S - - - - - - - - - - - - - - - - - - - - -
        GameRecord getGameRecord() throws RemoteException;
        MyShelfie getController(RemoteInterface remoteClient) throws RemoteException;
        Client getOwner() throws RemoteException;
        View getCurrentView() throws RemoteException;

}
