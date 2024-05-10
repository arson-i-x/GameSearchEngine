package UI;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JOptionPane;
import com.searchengine.Database;
import com.searchengine.Game;
import com.searchengine.Log;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

public class SearchWindow extends WINDOW implements Initializable {

    @FXML
    TextField query;

    static Game search;

    public SearchWindow()
    {
        open("SearchWindow.fxml");
    }

    @FXML
    public void backButton() 
    {
        SearchApp.display(new MainWindow());
    }

    @FXML
    public void query()
    {
        String input = query.getText();
        if (input.equals("")) {
            search();
            return;
        }
        try {
            SearchApp.addGame(input);
            query.clear();
            JOptionPane.showMessageDialog(null, input + " added to library");
        } catch (RuntimeException n) {
            Log.ERROR("GAME NOT FOUND");
            JOptionPane.showMessageDialog(null, "GAME NOT FOUND");
        }
    }

    @FXML
    public void search()
    {
        SearchApp.display(new GameWindow());
        SearchApp.changeWindowSize();
    }

    @FXML
    public void exit() 
    {
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Set<String> gameNames = Database.getAllNames();
        TextFields.bindAutoCompletion(query, gameNames);
    }
}
