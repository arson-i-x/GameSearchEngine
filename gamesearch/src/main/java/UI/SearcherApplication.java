package UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;

public class SearcherApplication extends Application {
    public void launchSearcherApp(String[] args) {
        launch(SearcherApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            Locale currentLocale = Locale.getDefault();

            WINDOW window = new WINDOW();
            window.open();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Error app controller!");
            e.printStackTrace();
        }
    }
}
