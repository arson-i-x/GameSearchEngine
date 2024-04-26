package UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WINDOW {
    private String fxmlResource = "WINDOW.fxml";
    private String title = "CS310 Project: Steam Game Searcher";
    protected Stage stage;
    protected FXMLLoader loader;
    public void open() {
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

    // This function activates when user click the yes button
    public void onLogin(){
        LoginqueryWindow window = new LoginqueryWindow();
        window.open();
    }

    // Function for the no button

    public void notLogin(){
        NoButtonWindow window = new NoButtonWindow();
        window.open();
    }
}
