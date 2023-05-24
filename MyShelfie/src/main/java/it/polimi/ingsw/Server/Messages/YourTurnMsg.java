package it.polimi.ingsw.Server.Messages;

/**
 * Message that allow the player to see the ChooseTilesFromBoardView.
 *
 * @author Irene Lo Presti
 */

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Client.View.ChooseTilesFromBoardView;
import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.CommonGoalCard;
import it.polimi.ingsw.Server.Model.PersonalGoalCard;
import it.polimi.ingsw.Server.Model.Tile;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class YourTurnMsg extends S2CMessage{

    public String nickname;
    public int maxTilesPickable;
    public Tile[][] boardSnapshot;
    public CommonGoalCard[] commonGoalCards;
    public PersonalGoalCard personalGoalCard;
    public boolean firstTurn;
    public ArrayList<String> playersNames;
    public Tile[][] shelfSnapshot;

    public YourTurnMsg(String nickname, int maxTilesPickable, Tile[][] boardSnapshot,
                       CommonGoalCard[] commonGoalCards, PersonalGoalCard personalGoalCard, boolean firstTurn,
                       ArrayList<String> playersNames, Tile[][] shelfSnapshot){
        this.nickname = nickname;
        this.maxTilesPickable = maxTilesPickable;
        this.boardSnapshot = new Tile[9][9];

        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                this.boardSnapshot[i][j] = boardSnapshot[i][j];

        this.personalGoalCard = personalGoalCard;
        this.commonGoalCards = commonGoalCards;
        this.firstTurn = firstTurn;
        this.playersNames = playersNames;

        this.shelfSnapshot = new Tile[6][5];
        for(int i=0; i<6; i++)
            for(int j=0; j<5; j++)
                this.shelfSnapshot[i][j] = shelfSnapshot[i][j];
    }


    @Override
    public void processMessage(ServerHandler serverHandler) {

        serverHandler.getClient().transitionToView(new ChooseTilesFromBoardView(this));
        serverHandler.getClient().getCurrentView().notifyView();
    }

    public String toString(){
        return "de sono un messaggio eeenoooorme";
    }
    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try {
            client.goToChooseTilesFromBoardView(this);
            if(!(nickname.equals(playersNames.get(0))&& firstTurn)){
                client.notifyView();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
