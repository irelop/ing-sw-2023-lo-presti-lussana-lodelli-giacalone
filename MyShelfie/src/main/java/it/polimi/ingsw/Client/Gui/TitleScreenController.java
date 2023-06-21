package it.polimi.ingsw.Client.Gui;

import it.polimi.ingsw.Server.Messages.S2CMessage;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.Objects;
import java.util.Random;

/**
 * Controller implementation for a "home page"
 * It contains a title image and buttons to start or exit the game
 * @see Controller
 */
public class TitleScreenController extends Controller {
    @FXML
    private GridPane shelf;

    /**
     * This JavaFX method allows to make some operations when the window is created.
     * In this case it calls loadRandomGrid()
     */
    @FXML
    private void initialize() {
        loadRandomGrid();
    }

    @Override
    public void build(S2CMessage message) {

    }

    @Override
    public void receiveAnswer(S2CMessage message) {

    }

    /**
     * This method is called when the startGameButton is clicked.
     * It switches the scene to the next section
     */
    @FXML
    private void startGame() {
        getOwner().getStageManager().loadNextStage("loginPlayer.fxml");
    }

    @FXML
    private void joinGame() {
        getOwner().getStageManager().loadNextStage("reconnection.fxml");
    }

    public void exit() {
        getOwner().getStageManager().getStage().close();
    }
    /**
     * This method creates a random colors filled grid
     * and set it as a custom background
     */
    @FXML
    private void loadRandomGrid() {

        String[][] grid = new String[4][5];
        Random r = new Random();

        // generating random colors grid
        for (int i = 3; i >= 0; i--) {
            for (int j = 0; j < 5; j++) {
                int rNumber = r.nextInt(1,8);
                switch (rNumber) {
                    case 1 -> grid[i][j] = "WHITE";
                    case 2 -> grid[i][j] = "PINK";
                    case 3 -> grid[i][j] = "BLUE";
                    case 4 -> grid[i][j] = "LIGHTBLUE";
                    case 5 -> grid[i][j] = "GREEN";
                    case 6 -> grid[i][j] = "YELLOW";
                    case 7 -> grid[i][j] = "BLANK";
                }
            }
        }

        // setting to blank each column that contains a
        // BLANK cell (according to "gravity constrains")
        boolean foundBlank = false;
        for (int j = 0; j < 5; j++) {
            for (int i = 3; i >= 0; i--) {
                if (Objects.equals(grid[i][j], "BLANK") && !foundBlank)
                    foundBlank = true;
                if (foundBlank)
                    grid[i][j] = "BLANK";
            }
            foundBlank = false;
        }

        // filling GridPane
        for (Node node: shelf.getChildren()) {
            String id = grid[shelf.getRowIndex(node).intValue()][shelf.getColumnIndex(node).intValue()];
            node.setId(id);
        }
    }

}