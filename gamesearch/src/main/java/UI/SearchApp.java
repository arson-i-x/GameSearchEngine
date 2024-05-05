package UI;

import javax.swing.JOptionPane;
import com.searchengine.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SearchApp extends Application 
{
    private static Parent root; 
    private static GameSearch searchInstance;
    private static Stage stage;

    public SearchApp() 
    {
        searchInstance = new GameSearch();
        if (!searchInstance.getUserData().isEmpty()) {
            GameWindow window = new GameWindow();
            root = window.getRoot();
        } else {
            root = new MainWindow().getRoot();
        }
    }

    public static void display(WINDOW window) {
        Stage source = (Stage) stage.getScene().getWindow();
        source.setScene(new Scene(window.getRoot()));
    }

    public static GameSearch getSearchInstance() 
    {
        return searchInstance;
    }

    public static Game getNextGame() 
    {
        return searchInstance.search();
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
        stage.setScene(new Scene(root)); 
        stage.show();
    }
}