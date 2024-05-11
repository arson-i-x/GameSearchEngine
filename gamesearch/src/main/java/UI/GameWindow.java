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
    private static List<Game> gameList;
    private static Integer currIndex;
    private WebEngine engine;
    private Game currGame;

    @FXML
    private WebView view;

    @FXML
    private TextField URL;
    
    @FXML
    private Label time;

    private static final String defaultpage = "https://google.com";

    public GameWindow() 
    {
        open("GameWindow.fxml");
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
        if (currIndex == null) {
            currIndex = gameList.size()-1;
        }
        currIndex++;
        try {
            currGame = gameList.get(currIndex);
            engine.load(currGame.getURL());
        } catch (IndexOutOfBoundsException e) {
            currIndex = -1;
            nextButton();
        }
    }

    @FXML
    public void backButton() 
    {
        if (currIndex == null) {
            currIndex = gameList.size()-1;
        }
        currIndex--;
        try {
            currGame = gameList.get(currIndex);
            engine.load(currGame.getURL());
        } catch (IndexOutOfBoundsException e) {
            currIndex = gameList.size();
            backButton();
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
        SearchApp.like(currGame);
        onNext();
    }

    @FXML
    public void onNext() 
    {
        view.setVisible(false);
        long startTime = 0, stoptime = 0;
        try {
            startTime = System.nanoTime();
            currGame = SearchApp.getNextGame();
            stoptime = System.nanoTime();
        } catch (NoSuchElementException e) {
            Log.MESSAGE("NO GAME FOUND"+e.getMessage());
            JOptionPane.showMessageDialog(null, "NO MORE GAMES IN DATABASE");
            exit();
        }
        engine.load(currGame.getURL());
        Long timecalc = stoptime - startTime;
        time.setText("Time taken: " + Long.valueOf(TimeUnit.NANOSECONDS.toMillis(timecalc)).toString() + "ms");
        gameList.add(currGame);
        currIndex = gameList.size()-1;
        view.setVisible(true);
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
        engine = view.getEngine();
        URL.textProperty().bind(engine.locationProperty());
        view.setVisible(false);
        gameList = new ArrayList<>();
        List<Long> idlist = SearchApp.getSearchInstance()  // Gets userdata games 
                .getUserData().getGames().keySet().stream().toList();// in order for browser
        for (Long id : idlist) {
            gameList.add(Database.getGame(id.toString()));
        }
        onNext();
    }
}
