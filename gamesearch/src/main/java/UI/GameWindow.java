package UI;

import java.net.URL;
import java.util.ResourceBundle;

import com.searchengine.Game;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class GameWindow extends WINDOW implements Initializable {

    @FXML
    private WebView view;
    private String testLink = "http://google.com";
    private WebEngine engine;
    private static Game game;

    public GameWindow() 
    {
        open("GameWindow.fxml");
    }

    public static GameWindow presentGame(Game game) 
    {
        GameWindow newWindow = new GameWindow();
        newWindow.loadGame(game);
        return newWindow;
    }

    public void loadGame(Game newgame) 
    {
        game = newgame;
        System.out.println(game.getName() + " is your game.");
    }

    public Game getGame() 
    {
        return game;
    }

    @FXML
    public void onDownload()
    {
        SearchApp.display(new DownloadWindow(game));
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
        newGameWindow.loadGame(SearchApp.getSearchInstance().nextGame());
        SearchApp.display(newGameWindow);
    }

    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        engine = view.getEngine();
        engine.load(game.getURL());
    }
}
