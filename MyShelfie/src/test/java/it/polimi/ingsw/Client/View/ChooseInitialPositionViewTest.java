package it.polimi.ingsw.Client.View;

import it.polimi.ingsw.Server.Model.Board;
import it.polimi.ingsw.Server.Model.Player;
import it.polimi.ingsw.Server.Model.ReadFileByLines;
import it.polimi.ingsw.Server.Model.Tile;
import it.polimi.ingsw.Server.Messages.BoardMsg;
import it.polimi.ingsw.Server.Messages.MaxTilesPickableMsg;
import it.polimi.ingsw.Server.Messages.PlayerNicknameMsg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChooseInitialPositionViewTest {
   public ChooseInitialPositionView chooseInitialPositionView;

   public BoardMsg boardMsg;
   public PlayerNicknameMsg playerNicknameMsg;
   public MaxTilesPickableMsg maxTilesPickableMsg;

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

        boardMsg = new BoardMsg(board);
        playerNicknameMsg = new PlayerNicknameMsg(new Player("player1"));
        maxTilesPickableMsg = new MaxTilesPickableMsg(3);

        chooseInitialPositionView = new ChooseInitialPositionView(playerNicknameMsg, maxTilesPickableMsg, boardMsg);

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