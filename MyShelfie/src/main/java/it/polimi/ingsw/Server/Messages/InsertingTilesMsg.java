package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.Exceptions.*;


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
    public InsertingTilesAnswer answer;

    public InsertingTilesMsg(int columnChosen, int[] chosenOrderIndexes) {
        this.columnChosen = columnChosen;
        this.chosenOrderIndexes = chosenOrderIndexes;
        this.answer = null;
    }

    /**
     * This method manages exceptions to create a message containing
     * a positive or negative answer to user's inputs
     * @param clientHandler: instance of server's ClientHandler
     */
    @Override
    public void processMessage(ClientHandler clientHandler){

        try{
            clientHandler.getController().insertingTiles(columnChosen,chosenOrderIndexes);
            answer = new InsertingTilesAnswer("",true);
            clientHandler.getController().endOfTheTurn();
        }catch(InvalidTileIndexInLittleHandException | NotEnoughSpaceInChosenColumnException e){
            answer = new InsertingTilesAnswer(e.toString(),false);
        }finally{
            clientHandler.sendMessageToClient(answer);
        }
    }
}
