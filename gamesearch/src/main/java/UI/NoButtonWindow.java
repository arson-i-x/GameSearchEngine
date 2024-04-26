package UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class NoButtonWindow {
    @FXML
    TextField ManualInput;
    @FXML
    TextArea ManualGameOutput;
    private String fxmlResource = "noButtonWindow.fxml";
    private String title = "CS310 Project: Steam Game Searcher";
    protected Stage stage;
    protected FXMLLoader loader;

    public void open(){
        FXMLLoader launchLoader = new FXMLLoader(getClass().getResource(fxmlResource));
        Parent root = null;
        try {
            root = launchLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);

        Stage launchStage = new Stage();
        launchStage.setScene(scene);

        launchStage.setTitle(title);
        launchStage.show();
        this.loader = launchLoader;
        this.stage = launchStage;
    }
}
