package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.Exceptions.*;

public class PlayerChoiceMsg extends C2SMessage{

    public int initialRow;
    public int initialColumn;
    public char direction;
    public int numberOfTiles;
    public int maxTilesPickable;
    public PlayerChoiceAnswer playerChoiceAnswer;


    public PlayerChoiceMsg(int initialRow, int initialColumn, char direction, int numberOfTiles, int maxTilesPickable){
        this.initialRow = initialRow;
        this.initialColumn = initialColumn;
        this.direction = direction;
        this.numberOfTiles = numberOfTiles;
        this.playerChoiceAnswer = null;
        this.maxTilesPickable = maxTilesPickable;
    }

    @Override
    public void processMessage(ClientHandler clientHandler){
        try{
            clientHandler.getController().getBoard().checkDirectionAndNumberOfTiles(direction, numberOfTiles, initialRow, initialColumn, maxTilesPickable);
            playerChoiceAnswer = new PlayerChoiceAnswer("",true);
            clientHandler.getController().getPlayerChoice(initialRow, initialColumn, direction, numberOfTiles);

        }catch(OutOfBoardException | InvalidPositionException | InvalidCellException | EmptyCellException
                | InvalidNumberOfTilesException | InvalidDirectionException e){
            playerChoiceAnswer = new PlayerChoiceAnswer(e.toString(),false);
        }finally{
            clientHandler.sendMessageToClient(playerChoiceAnswer);
        }
    }
}
