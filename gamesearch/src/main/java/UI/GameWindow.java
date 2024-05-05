package UI;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import com.searchengine.Database;
import com.searchengine.Game;
import com.searchengine.Log;
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
    private Game thisGame;

    @FXML
    private WebView view;

    @FXML
    private TextField URL;
    
    @FXML
    private Label time;

    public GameWindow() 
    {
        open("GameWindow.fxml");
    }

    @FXML
    public void deleteLibraryData() 
    {
        SearchApp.getSearchInstance().clearUserData();
        gameList.clear();
        SearchApp.display(new MainWindow());
    }

    @FXML
    public void clearLikes() 
    {
        SearchApp.getSearchInstance().clearLikes();
        gameList.clear();
    }

    @FXML
    public void nextButton() 
    {
        if (currIndex == gameList.size()-1 || currIndex == -1) {
            return;
        }

        thisGame = Database.getGame(gameList.get(++currIndex).toString());
        String lastURL = thisGame.getURL();
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
            thisGame = Database.getGame(gameList.get(--currIndex).toString());
            String lastURL = thisGame.getURL();
            engine.load(lastURL);
            URL.setText(lastURL);
        } catch (IndexOutOfBoundsException e) {
            returnToSearch();
        }
    }

    @FXML
    public void returnToSearch()
    {
        SearchApp.display(new SearchWindow());
    }

    @FXML
    public void onLike() 
    {
        SearchApp.getSearchInstance().like(thisGame);
        onNext();
    }

    @FXML
    public void onNext() 
    {
        long startTime = System.nanoTime();
        thisGame = SearchApp.getSearchInstance().search();
        long stopTime = System.nanoTime();
        Log.MESSAGE(thisGame.getName() + " is your game.");
        Long timecalc = stopTime - startTime;
        time.setText("Time taken: " + Long.valueOf(TimeUnit.NANOSECONDS.toMillis(timecalc)).toString() + "ms");
        engine = view.getEngine();
        URL.setText(thisGame.getURL());
        gameList.add(thisGame.getGameID());
        currIndex = gameList.size()-1;
        engine.load(URL.getText());
    }

    @FXML
    public void onEnter() 
    {
        engine.load("https://"+URL.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        gameList = new ArrayList<Long>(SearchApp.getSearchInstance()  // Gets userdata games 
                .getUserData().getGames().keySet().stream().toList());// in order for browser
        onNext();
    }
}
