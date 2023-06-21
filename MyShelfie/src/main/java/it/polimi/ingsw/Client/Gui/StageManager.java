package it.polimi.ingsw.Client.Gui;

import it.polimi.ingsw.Client.Client;
import it.polimi.ingsw.Server.Messages.S2CMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * This utility class is a Singleton that keeps track of the stage,
 * the controller and the client. It also helps during transitions
 * between two different scenes and during message exchange between
 * client with GUI and server
 */
public class StageManager {
    private static Stage stage;
    private Controller controller;
    private Client owner;
    private static StageManager instance;


    public StageManager() {
        controller = new TitleScreenController();
        owner = new Client();
    }

    public static StageManager getInstance() {
        if (instance == null)
            instance = new StageManager();
        return instance;
    }

    public static Stage getStage() {
        return stage;
    }
    public static void setStage(Stage stage) {
        StageManager.stage = stage;
        StageManager.stage.setResizable(false);
    }
    public Controller getController() {
        return controller;
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }
    public Client getOwner() {
        return owner;
    }
    public void setOwner(Client owner) {
        this.owner = owner;
    }

    /**
     * This method is called once at the beginning.
     * It opens a window containing the Title Screen scene, taken from
     * the .fxml file in resources
     * @throws IOException
     */
    public void firstLaunch() throws IOException {

        FXMLLoader loader = new FXMLLoader();

        URL url = StageManager.class.getResource("/fxml/titleScreen.fxml");
        System.out.println(url);

        loader.setLocation(url);
        StageManager.getStage().setTitle("MyShelfie");

        Parent root = loader.load();
        controller = loader.getController();
        controller.setOwner(owner);

        StageManager.getStage().setScene(new Scene(root));
        StageManager.getStage().show();
    }

    /**
     * This method loads the next stage, building it with the
     * message passed as parameter
     * @param message: server message
     * @param fxmlAddress: relative path to .fxml file
     */
    public void loadNextStage(S2CMessage message, String fxmlAddress){
        FXMLLoader loader;
        try {
            loader = owner.mainApp.prepareScene(fxmlAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        controller = loader.getController();

        controller.setOwner(owner);

        controller.build(message);

        AnchorPane root = loader.getRoot();

        owner.mainApp.transitionScene(root);
    }

    /**
     * This method loads the next stage
     * @param fxmlAddress: relative path to .fxml file
     */
    public void loadNextStage(String fxmlAddress){
        JavaGUI javaGUI = new JavaGUI();
        FXMLLoader loader;
        try {
            loader = javaGUI.prepareScene(fxmlAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        controller = loader.getController();

        controller.setOwner(owner);

        AnchorPane root = loader.getRoot();

        javaGUI.transitionScene(root);
    }


}
