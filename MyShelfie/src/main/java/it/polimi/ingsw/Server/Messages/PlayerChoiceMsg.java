package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.Exceptions.*;

/**
 * This class creates a message with the initial row, the initial column, the number of tiles
 * and the direction chosen by the player,and it sends it to the server.
 *
 * @author Irene Lo Presti
 */

public class PlayerChoiceMsg extends C2SMessage{

    public int initialRow;
    public int initialColumn;
    public char direction;
    public int numberOfTiles;
    public int maxTilesPickable;


    public PlayerChoiceMsg(int initialRow, int initialColumn, char direction, int numberOfTiles, int maxTilesPickable){
        this.initialRow = initialRow;
        this.initialColumn = initialColumn;
        this.direction = direction;
        this.numberOfTiles = numberOfTiles;
        this.maxTilesPickable = maxTilesPickable;
    }

    /**
     * If the choice is valid then this method call the method getPlayerChoice in the controller in order to pick
     * the tiles from the board, otherwise it catch the exception and sends a message to the view in order to
     * give the player the possibility to choose again.
     * @param clientHandler: instance of ClientHandler
     */
    @Override
    public void processMessage(ClientHandler clientHandler){
        S2CMessage playerChoiceAnswer;
        try{
            clientHandler.getController().getBoard().checkDirectionAndNumberOfTiles(direction, numberOfTiles, initialRow, initialColumn, maxTilesPickable);
            playerChoiceAnswer = new PlayerChoiceAnswer("",true);
            clientHandler.getController().getPlayerChoice(initialRow, initialColumn, direction, numberOfTiles);

        }catch(OutOfBoardException | InvalidPositionException | InvalidCellException | EmptyCellException
                | InvalidNumberOfTilesException | InvalidDirectionException e){
            playerChoiceAnswer = new PlayerChoiceAnswer(e.toString(),false);
        }
        clientHandler.sendMessageToClient(playerChoiceAnswer);
    }
}
