package UI;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class WINDOW {

    private FXMLLoader loader;

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

    // GUI WINDOW TEST
    public static void main (String[] args) 
    {

        SearchApp.launch(SearchApp.class, args);

    }
}
