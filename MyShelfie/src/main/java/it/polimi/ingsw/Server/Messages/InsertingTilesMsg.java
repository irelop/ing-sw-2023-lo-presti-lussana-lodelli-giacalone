package it.polimi.ingsw.Server.Messages;

import it.polimi.ingsw.Client.ServerHandler;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.Exceptions.*;

public class InsertingTilesMsg extends C2SMessage {

    public int columnChosen;
    public int[] chosenOrderIndexes;
    public InsertingTilesAnswer answer;

    public InsertingTilesMsg(int columnChosen, int[] chosenOrderIndexes) {
        this.columnChosen = columnChosen;
        this.chosenOrderIndexes = chosenOrderIndexes;
        this.answer = null;
    }


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
