package UI;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import com.searchengine.Database;
import com.searchengine.Game;
import com.searchengine.Log;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class GameWindow extends WINDOW implements Initializable {
    
    // stores all games returned
    private static List<Long> gameList;
    private static int currIndex = -1;

    // instance variables
    private WebEngine engine;
    private Game game;

    @FXML
    private WebView view;

    @FXML
    private TextField URL;
    
    @FXML
    private Label time;

    private static final String defaultpage = "google.com";

    public GameWindow() 
    {
        open("GameWindow.fxml");
    }

    @FXML
    public void maximize() 
    {
        SearchApp.changeWindowSize();
    }

    @FXML
    public void deleteLibraryData() 
    {
        engine.load(defaultpage);
        SearchApp.clearUserData();
        gameList.clear();
        SearchApp.display(new MainWindow());
    }

    @FXML
    public void clearLikes() 
    {
        SearchApp.clearLikes();
        gameList.clear();
    }

    @FXML
    public void nextButton() 
    {
        if (currIndex == gameList.size()-1 || currIndex == -1) {
            return;
        }

        game = Database.getGame(gameList.get(++currIndex).toString());
        String lastURL = game.getURL();
        engine.load(lastURL);
        URL.setText(lastURL);
    }

    @FXML
    public void backButton() 
    {
        if (currIndex == -1) {
            currIndex = gameList.size()-1;
        }

        try {
            game = Database.getGame(gameList.get(--currIndex).toString());
            String lastURL = game.getURL();
            engine.load(lastURL);
            URL.setText(lastURL);
        } catch (IndexOutOfBoundsException e) {
            returnToSearch();
        }
    }

    @FXML
    public void returnToSearch()
    {
        engine.load(defaultpage);
        SearchApp.display(new SearchWindow());
    }

    @FXML
    public void onLike() 
    {
        SearchApp.like(game);
        onNext();
    }

    @FXML
    public void onNext() 
    {
        view.setVisible(false);
        long startTime = 0, stoptime = 0;
        try {
            startTime = System.nanoTime();
            game = SearchApp.getNextGame();
            stoptime = System.nanoTime();
        } catch (NoSuchElementException e) {
            Log.MESSAGE("NO GAME FOUND"+e.getMessage());
            JOptionPane.showMessageDialog(null, "NO MORE GAMES IN DATABASE");
            exit();
        }

        Log.MESSAGE(game.getName() + " is your game.");
        Long timecalc = stoptime - startTime;
        time.setText("Time taken: " + Long.valueOf(TimeUnit.NANOSECONDS.toMillis(timecalc)).toString() + "ms");
        engine = view.getEngine();
        URL.setText(game.getURL());
        gameList.add(game.getGameID());
        currIndex = gameList.size()-1;
        view.setVisible(true);
        engine.load(URL.getText());
    }

    @FXML
    public void onEnter() 
    {
        engine.load("https://"+URL.getText());
    }

    @FXML
    public void exit() 
    {
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        view.setVisible(false);
        gameList = new ArrayList<Long>(SearchApp.getSearchInstance()  // Gets userdata games 
                .getUserData().getGames().keySet().stream().toList());// in order for browser
        onNext();
    }
}
