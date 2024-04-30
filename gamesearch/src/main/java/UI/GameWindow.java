package UI;

import java.net.URL;
import java.util.ResourceBundle;

import com.searchengine.Game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class GameWindow extends WINDOW implements Initializable {

    @FXML
    private WebView view;
    private WebEngine engine;
    private static Game game;
    private static Long timecalc;
    
    @FXML
    private Label time;

    public GameWindow() 
    {
        open("GameWindow.fxml");
    }

    public static GameWindow presentGame(Game game, Long timecalc) 
    {
        GameWindow newWindow = new GameWindow();
        newWindow.loadGame(game, timecalc);
        return newWindow;
    }

    public void loadGame(Game newgame, Long time) 
    {
        game = newgame;
        timecalc = time;
        System.out.println(game.getName() + " is your game.");
    }

    public Game getGame() 
    {
        return game;
    }

    @FXML
    public void onDownload()
    {
        Platform.exit();
    }

    @FXML
    public void onLike() 
    {
        SearchApp.getSearchInstance().like(game);
        onNext();
    }

    @FXML
    public void onNext() 
    {
        GameWindow newGameWindow = new GameWindow();
        long startTime = System.nanoTime();
        Game game = SearchApp.getSearchInstance().nextGame();
        long stopTime = System.nanoTime();
        newGameWindow.loadGame(game, stopTime - startTime);
        SearchApp.display(newGameWindow);
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        engine = view.getEngine();
        engine.load(game.getURL());
        time.setText(timecalc.toString());
    }
}
