package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.InsertInShelfView;
import it.polimi.ingsw.Server.Model.CommonGoalCard;
import it.polimi.ingsw.Server.Model.PersonalGoalCard;
import it.polimi.ingsw.Server.Model.Tile;

import java.util.ArrayList;

/**
 * This class represents a message sent from server to client.
 * It sends some info to InsertInShelf view, such as current player's shelf, goal cards
 * and tiles picked from the board
 *
 * @author Riccardo Lodelli
 */

public class MyShelfMsg extends S2CMessage {

    private Tile[][] shelf;
    private ArrayList<Tile> littleHand;
    public CommonGoalCard[] commonGoalCards;
    public PersonalGoalCard personalGoalCard;

    public MyShelfMsg(Tile[][] shelf,ArrayList<Tile> littleHand,CommonGoalCard[] commonGoalCards, PersonalGoalCard personalGoalCard) {
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
        serverHandler.getClient().transitionToView(new InsertInShelfView(this));
    }

    public Tile[][] getShelf() {
        return shelf;
    }

    public ArrayList<Tile> getLittleHand() {
        return littleHand;
    }
}