package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.InsertInShelfView;
import it.polimi.ingsw.Client.View.WaitingView;
import it.polimi.ingsw.Server.Model.CommonGoalCard;
import it.polimi.ingsw.Server.Model.PersonalGoalCard;
import it.polimi.ingsw.Server.Model.Tile;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class represents a message sent from server to client.
 * It sends some info to InsertInShelf view, such as current player's shelf, goal cards
 * and tiles picked from the board
 *
 * @author Riccardo Lodelli
 */

public class ToShelfMsg extends S2CMessage {

    public String[] commonPoints;
    private Tile[][] shelf;
    private ArrayList<Tile> littleHand;
    public CommonGoalCard[] commonGoalCards;
    public PersonalGoalCard personalGoalCard;
    public Tile[][] getShelf() {
        return shelf;
    }

    public ArrayList<Tile> getLittleHand() {
        return littleHand;
    }
    public ToShelfMsg(Tile[][] shelf, ArrayList<Tile> littleHand, CommonGoalCard[] commonGoalCards, PersonalGoalCard personalGoalCard) {
        this.shelf = shelf;
        this.littleHand = littleHand;
        this.commonGoalCards = commonGoalCards;
        this.commonPoints = new String[2];
        for (int i = 0; i < 2; i++) {
            commonPoints[i] = commonGoalCards[i].printAvailableScore();
        }
        this.personalGoalCard = personalGoalCard;
    }

    /**
     * this method set the current view to InsertInShelfView
     * @param serverHandler: instance of the ServerHandler
     */
    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getOwner().transitionToView(new InsertInShelfView(this));
        if(serverHandler.getOwner().getCurrentView().getClass() == WaitingView.class)
            serverHandler.getOwner().getCurrentView().notifyView();
    }


    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            client.goToInsertInShelfView(this);
            if(client.getCurrentView().getClass() == WaitingView.class)
                client.notifyView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}