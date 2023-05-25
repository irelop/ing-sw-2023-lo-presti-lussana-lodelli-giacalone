package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.Exceptions.*;
import it.polimi.ingsw.Server.RemoteInterface;

import java.rmi.RemoteException;


/**
 * This class represents a message sent from client to server.
 * It brings user's inputs inserted in InsertInShelfView to the server
 * to check if they are right.
 * Then it creates an answer message form the server
 * @see InsertingTilesAnswer
 *
 * @author Riccardo Lodelli
 */
public class InsertingTilesMsg extends C2SMessage {

    public int columnChosen;
    public int[] chosenOrderIndexes;
    //public InsertingTilesAnswer answer;

    public InsertingTilesMsg(int columnChosen, int[] chosenOrderIndexes) {
        this.columnChosen = columnChosen;
        this.chosenOrderIndexes = chosenOrderIndexes;
    }

    /**
     * This method manages exceptions to create a message containing
     * a positive or negative answer to user's inputs
     * @param clientHandler: instance of server's ClientHandler
     */
    @Override
    public void processMessage(ClientHandler clientHandler){
        S2CMessage answer;
        try{
            clientHandler.getController().insertingTiles(columnChosen,chosenOrderIndexes);
            answer = new InsertingTilesAnswer("",true);
            clientHandler.getController().computeTurnScore();
        }catch(InvalidTileIndexInLittleHandException | NotEnoughSpaceInChosenColumnException e){
            answer = new InsertingTilesAnswer(e.toString(),false);
        }
        clientHandler.sendMessageToClient(answer);
    }

    @Override
    public void processMessage(RemoteInterface server, RemoteInterface client){
        try{
            S2CMessage answer;
            try {
                server.getController().insertingTiles(columnChosen, chosenOrderIndexes);
                answer = new InsertingTilesAnswer("", true);
                server.getController().computeTurnScore();
            } catch (InvalidTileIndexInLittleHandException | NotEnoughSpaceInChosenColumnException e) {
                answer = new InsertingTilesAnswer(e.toString(), false);
            }
            client.sendMessageToClient(answer);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }
}
