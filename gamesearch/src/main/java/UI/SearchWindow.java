package UI;

import java.net.URL;
import java.util.ResourceBundle;
import javax.naming.NameNotFoundException;
import javax.swing.JOptionPane;
import com.searchengine.Game;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.web.*;

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
    }
}
