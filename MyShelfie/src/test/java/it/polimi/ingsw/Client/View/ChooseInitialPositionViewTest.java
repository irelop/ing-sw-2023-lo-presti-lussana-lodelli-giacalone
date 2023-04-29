package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Messages.YourTurnMsg;
import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.ReadFileByLines;
import it.polimi.ingsw.Server.Model.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//DA CANCELLARE
public class ChooseInitialPositionViewTest {
   public ChooseInitialPositionView chooseInitialPositionView;

   public YourTurnMsg yourTurnMsg;

    ReadFileByLines reader;

    @BeforeEach
    void setUp() {
        Board board;
        Tile[][] grid = new Tile[9][9];

        board = new Board();
        Board.getBoardInstance();

        reader = new ReadFileByLines();
        reader.readFrom("src/test/testFiles/board_3p.txt");

        for (int i = 0; i < 9; i++) {

            String row = ReadFileByLines.getLine();

            String[] values = row.replaceAll("\\{", "")
                    .replaceAll("}", "")
                    .split(", ");

            for (int j = 0; j < 9; j++)
                grid[i][j] = Tile.valueOf(values[j]);
        }
        //set the grid in board with constructor from file
        board.initFromMatrix(grid);

        yourTurnMsg = new YourTurnMsg("player1", 3, Board.getBoardGrid());

        chooseInitialPositionView = new ChooseInitialPositionView(yourTurnMsg);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void printBoard_visualTest(){
        chooseInitialPositionView.printBoard();
    }

    @Test
    public void run_visualTest(){chooseInitialPositionView.run();}
}