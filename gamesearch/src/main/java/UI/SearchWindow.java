package UI;

import java.net.URL;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.naming.NameNotFoundException;
import javax.swing.JOptionPane;

import com.searchengine.Database;
import com.searchengine.Game;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.web.*;
import org.controlsfx.control.textfield.TextFields;
import org.w3c.dom.Text;

public class SearchWindow extends WINDOW implements Initializable {

    @FXML
    TextField query;

    @FXML WebView view;

    WebEngine engine;

    static Game search;

    public SearchWindow()
    {
        open("SearchWindow.fxml");
    }

    @FXML
    public void query()
    {
//        Database.init();
//        List<String> gameNames = new ArrayList<>();
//        for (Game game : Database.getAllGames()) {
//            gameNames.add(game.getName());
//        }
//        TextFields.bindAutoCompletion(query, gameNames);
        try {
            engine.load(SearchApp.getSearchInstance().getUserData().addGame(query.getText()));
        } catch (NameNotFoundException nameNotFoundException) {
            JOptionPane.showMessageDialog(null, "GAME NOT FOUND");
        }
    }

    @FXML
    public void search()
    {
        SearchApp.display(GameWindow.presentGame(SearchApp.getSearchInstance().nextGame(), (long)1));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = view.getEngine();
        Database.init();
        List<String> gameNames = new ArrayList<>();
        for (Game game : Database.getAllGames()) {
            gameNames.add(game.getName());
        }
        TextFields.bindAutoCompletion(query, gameNames);
    }
}
