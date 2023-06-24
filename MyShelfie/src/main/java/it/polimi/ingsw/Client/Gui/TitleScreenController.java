package it.polimi.ingsw.Client.Gui;

import it.polimi.ingsw.Server.Messages.S2CMessage;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.Objects;
import java.util.Random;

/**
 * Controller implementation for a "home page"
 * It contains a title image and buttons to start or exit the game
 * @see Controller
 */
public class TitleScreenController extends Controller{
    @FXML
    private GridPane shelf;
    @FXML
    TextArea rulesText;
    @FXML
    Pane rulesPane;
    @FXML
    Button closeRules;

    @Override
    public void build(S2CMessage message) {

    }
    /**
     * This JavaFX method allows to make some operations when the window is created.
     * In this case it calls loadRandomGrid()
     */
    @FXML
    private void initialize() {
        loadRandomGrid();

        rulesText.setText(
                "RULES TO PLAY MY SHELFIE: \n"+
                "Goal of the game"+
                "Players take item tiles from the living room (the board) and place them in\n "+
                "their bookshelves to score points; the game ends when a player\n" +
                "completely fills their bookshelf. The player with more points at\n" +
                "the end will win the game. There are 4 ways to score points:"+
                "\n1) Personal goal card:"+
                "\tThe personal goal card grants points if you match the highlighted spaces\n" +
                "\twith the corresponding item tiles. You will see the personal goal directly in your\n" +
                "\tpersonal shelf"+
                "\n2) Common goal cards:"+
                "\tThe common goal cards grant points to the players who achieve the illustrated\n" +
                "\tpattern. Every common goal card has a detailed description, so don't worry!\n" +
                "\tThe first player to achieve the personal goal wins 8 points, the second one 6 points\n" +
                "\tthe third one 4 points and the last one 2 points. It isn't possible to achieve the\n" +
                "\tsame goal multiple times."+
                 "\n3) Adjacent Item tiles:"+
                "\tGroups of adjacent item tiles of the same color on your bookshelf grant points\n" +
                "\tdepending on how many tiles are connected (with one side touching).\n" +
                "\tPoints:\n" +
                "\t\t 3 tiles connected: 2 points\n" +
                "\t\t 4 tiles connected: 3 points\n" +
                "\t\t 5 tiles connected: 5 points\n" +
                "\t\t6+ tiles connected: 8 points\n" +
                "\tLet's make an example:"+
                "\n4) Game-end trigger:"+
                "\tThe first player who completely fills their bookshelf scores 1 additional point."+

                "\n\nGame play:"+
                "The game is divided in turns that take place in a clockwise order starting from\n" +
                "the first player. The first player is chosen randomly.\n" +
                "During your turn, you must take 1, 2 o 3 item tiles from living room board, following \n" +
                "these rules:\n"+
                "\tThe tiles you take must be adjacent to each other and form a straight line.\n"+
                "\tAll the tiles you take have at least one side free (not touching directly other\n" +
                "\ttiles) at the beginning of your turn (i.e. you cannot take a tile that becomes free\n" +
                "\tafter your first pick).\n"+
                "\tThen, you must place all the tiles you've picked into 1 column of your bookshelf.\n" +
                "\tYou can decide the order, but you cannot place tiles in more than 1 column in a single turn.\n"+
                "Note: You cannot take tiles if you don't have enough available space in your bookshelf\n"+
                "At the end of a turn, if there are only item tiles without any other adjacent tile on\n" +
                "the board, the board will automatically refill.\n\n"+
                "Game end:"+
                "\tThe first player who fills all the spaces of their bookshelf takes the end game\n" +
                "\ttoken. The game continues until the last player(the one before the first)\n" +
                "\thas played their turn.\n\n");
        rulesPane.setVisible(false);
        closeRules.setVisible(false);
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

    /**
     * This method is called when the joinExistingGame button is clicked.
     * It switches the scene to the next section
     */
    @FXML
    private void joinGame() {
        getOwner().getStageManager().loadNextStage("reconnection.fxml");
    }

    /**
     * This method is called when the exit button is clicked.
     * It closes the window
     */
    public void exit() {
        getOwner().getStageManager().getStage().close();
    }

    /**
     * This method is called when the rules button is clicked.
     * It makes appear the rules of the game
     */
    public void showRules(){
        rulesPane.setVisible(true);
        closeRules.setVisible(true);
    }

    public void closeRulesPane(){
        rulesPane.setVisible(false);
        closeRules.setVisible(false);
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