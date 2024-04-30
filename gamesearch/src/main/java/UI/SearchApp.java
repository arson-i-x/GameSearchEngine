package UI;

import javax.swing.JOptionPane;
import com.searchengine.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SearchApp extends Application 
{
    public static SearchApp instance;
    private static GameSearchApplication search;
    private static Stage stage;

    public SearchApp() 
    {
        instance = this;
        search = new GameSearchApplication();
    }

    public static void launchApp() 
    {
        try {
            launch(SearchApp.class);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,"REVISING SEARCH");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws RuntimeException
    {
        if (Database.size() < 1) 
        {
            throw new RuntimeException();
        }
        stage = primaryStage;
        stage.setTitle("GAME SEARCH UI");
        stage.setScene(new Scene(new MainWindow().getRoot())); 
        stage.show();
    }

    public static void display(WINDOW window) {
        Stage source = (Stage) stage.getScene().getWindow();
        source.setScene(new Scene(window.getRoot()));
    }

    public static GameSearchApplication getSearchInstance() 
    {
        return search;
    }
}