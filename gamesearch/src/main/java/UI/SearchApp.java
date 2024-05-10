package UI;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JOptionPane;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.searchengine.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SearchApp extends Application 
{
    private static Parent root; 
    private static GameSearch searchInstance;
    private static Stage stage;
    private static boolean noResize;
    private static double x = 0;
    private static double y = 0;

    public SearchApp() 
    {
        searchInstance = new GameSearch();
        if (!searchInstance.getUserData().isEmpty()) {
            root = new GameWindow().getRoot();
            noResize = false;
        } else {
            root = new MainWindow().getRoot();
            noResize = true;
        }
    }

    public static void changeWindowSize() 
    {
        if (!stage.isFullScreen()) {
            stage.setFullScreen(true);
            return;
        }
        stage.setFullScreen(false);
    }

    public static void display(WINDOW window) {
        root = window.getRoot();
        stage.setScene(new Scene(root));
        SearchApp.makeDraggable();
        stage.centerOnScreen();
        if (noResize) {
            stage.setResizable(false);
        } else {
            stage.setResizable(true);
        }
    }

    public static void makeDraggable() 
    {
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
            }
        });
    }

    public static GameSearch getSearchInstance() 
    {
        return searchInstance;
    } 

    public static Game getNextGame() throws NoSuchElementException
    {
        return searchInstance.search();
    } 

    public static void addGame(Game game) throws SteamApiException, IOException 
    {
        searchInstance.getUserData().addGame(game);
    } 

    public static void addGame(String query) throws RuntimeException 
    {
        searchInstance.getUserData().addGame(query);
    } 

    public static void clearUserData() 
    {
        searchInstance.clearUserData();
    }

    public static void clearLikes() 
    {
        searchInstance.clearLikes();
    }

    public static void like(Game game) 
    {
        searchInstance.like(game);
    }
    
    public static void login(String id) throws SteamApiException, IOException 
    {
        searchInstance.login(id);
    } 

    public static void launchApp() 
    {
        try {
            launch();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,"FATAL ERROR");
            e.printStackTrace();
        }
    }

    @FXML
    public void exit() 
    {
        Platform.exit();
    }

    @Override
    public void start(Stage primaryStage) throws RuntimeException
    {
        if (Database.size() < 1) 
        {
            throw new RuntimeException();
        }

        stage = primaryStage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root)); 
        stage.show();
   
        SearchApp.makeDraggable();

        if (!noResize) {
            stage.setFullScreen(true);
        }
    }
}