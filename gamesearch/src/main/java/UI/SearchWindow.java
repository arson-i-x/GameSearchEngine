package UI;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JOptionPane;
import com.searchengine.Database;
import com.searchengine.Game;
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
            SearchApp.getSearchInstance().getUserData().addGame(input);
            query.clear();
        } catch (RuntimeException n) {
            JOptionPane.showMessageDialog(null, "GAME NOT FOUND\n"+n.getMessage());
        }
    }

    @FXML
    public void search()
    {
        SearchApp.display(new GameWindow());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Set<String> gameNames = Database.getAllNames();
        TextFields.bindAutoCompletion(query, gameNames);
    }
}
