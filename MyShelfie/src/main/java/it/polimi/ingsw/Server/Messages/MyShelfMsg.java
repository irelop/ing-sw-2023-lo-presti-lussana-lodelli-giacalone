package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.InsertInShelfView;
import it.polimi.ingsw.Server.Model.Tile;

import java.util.ArrayList;

public class MyShelfMsg extends S2CMessage {

    private Tile[][] shelf;
    private ArrayList<Tile> littleHand;

    public MyShelfMsg(Tile[][] shelf,ArrayList<Tile> littleHand) {
        super(parent);
        this.shelf = shelf;
        this.littleHand = littleHand;
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