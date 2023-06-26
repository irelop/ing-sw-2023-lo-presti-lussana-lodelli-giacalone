package it.polimi.ingsw.Client.view.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represent the GuiApplication, it manages the
 * preparation of the next Scene and the transition to it
 */

public class JavaGUI extends Application {

    public void start(Stage stage) throws Exception{

        StageManager.setStage(stage);

        Platform.runLater(
                ()->{
                    try{
                        StageManager.getInstance().firstLaunch();
                    }catch (Exception ignored){}
                }
        );


    }

    /**
     * This method is called before the scene transition.
     * @return fxmlLoader: an object that contains all information for the creation of the new scene
     * @param fxml: the name of the file FXML with the next scene
     * @throws IOException: Input/Output exception
     */
    public FXMLLoader prepareScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
        fxmlLoader.load();
        return fxmlLoader;
    }

    /**
     * This method set the new scene at the stage
     * @param newRoot: the root of the next scene
     *
     */
    public void transitionScene(AnchorPane newRoot) {
        if(StageManager.getStage().getScene() == null) System.out.println("la scena è nulla");
        Platform.runLater(
                ()->{
                    try{
                        StageManager.getStage().getScene().setRoot(newRoot);
                    }catch (Exception e){
                        System.out.println("sono nell'eccezione");
                        e.printStackTrace();
                    }
                }
        );
    }

    public void transitionScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/" + fxml));
        Parent newRoot = fxmlLoader.load();
        StageManager.getStage().setScene(new Scene(newRoot));
    }

    public static void main(String args[]){
        launch(args);
    }
}
