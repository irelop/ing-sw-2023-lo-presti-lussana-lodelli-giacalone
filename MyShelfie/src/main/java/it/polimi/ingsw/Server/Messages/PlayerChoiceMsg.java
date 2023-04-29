package it.polimi.ingsw.Server.Messages;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Model.Exceptions.*;

/**
 * This class creates a message with the direction and the number of tiles chosen by the player and
 * it sends it to the server
 *
 * @author Irene Lo Presti
 */

public class PlayerChoiceMsg extends C2SMessage{

    public int initialRow;
    public int initialColumn;
    public char direction;
    public int numberOfTiles;
    public int maxTilesPickable;
    public PlayerChoiceAnswer playerChoiceAnswer;

    /**
     * OVERVIEW: constructor method, it creates the message with the direction and the number of tiles
     * @param direction: char that represent the chosen direction
     * @param numberOfTiles: number of tiles chosen
     */
    public PlayerChoiceMsg(int initialRow, int initialColumn, char direction, int numberOfTiles, int maxTilesPickable){
        this.initialRow = initialRow;
        this.initialColumn = initialColumn;
        this.direction = direction;
        this.numberOfTiles = numberOfTiles;
        this.playerChoiceAnswer = null;
        this.maxTilesPickable = maxTilesPickable;
    }

    /**
     * OVERVIEW: this method sends the message to the server with the server handler
     * @see ClientHandler
     * @param clientHandler: instance of the ServerHandler
     */
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
