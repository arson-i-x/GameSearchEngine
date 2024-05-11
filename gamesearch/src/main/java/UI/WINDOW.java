package UI;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class WINDOW {

    private FXMLLoader loader;

    @FXML
    AnchorPane anchor;

    double x = 0, y = 0;

    // Open and returns a scene root using FXML Loader
    public void open(String path) throws RuntimeException
    {
        loader = new FXMLLoader(getClass().getResource(path));
    }

    public Parent getRoot() 
    {
        try {
            return loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    @FXML
    public void maximize() 
    {
        SearchApp.changeWindowSize();
    }

    @FXML
    public void minimize() 
    {
        SearchApp.minimize();
    }
}
