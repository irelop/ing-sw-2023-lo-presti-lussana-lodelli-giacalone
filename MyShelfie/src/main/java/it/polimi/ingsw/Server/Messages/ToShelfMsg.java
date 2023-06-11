package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.InsertInShelfView;
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
        this.personalGoalCard = personalGoalCard;
    }

    /**
     * this method set the current view to InsertInShelfView
     * @param serverHandler: instance of the ServerHandler
     */
    @Override
    public void processMessage(ServerHandler serverHandler) {
        serverHandler.getOwner().transitionToView(new InsertInShelfView(this));
    }


    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            client.goToInsertInShelfView(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}