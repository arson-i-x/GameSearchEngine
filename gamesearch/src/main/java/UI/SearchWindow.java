package UI;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JOptionPane;
import com.searchengine.Database;
import com.searchengine.Game;
import com.searchengine.Log;
import com.searchengine.UserData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;

public class SearchWindow extends WINDOW implements Initializable {

    @FXML
    TextField query;

    @FXML
    TextArea gameList;

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
            updateGames();
            query.clear();
        } catch (RuntimeException n) {
            Log.ERROR("GAME NOT FOUND");
            JOptionPane.showMessageDialog(null, "GAME NOT FOUND");
        }
    }

    @FXML
    public void search()
    {
        SearchApp.display(new GameWindow());
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
        updateGames();
    }

    private void updateGames() // prints game list to window
    {
        UserData data = SearchApp.getSearchInstance().getUserData();
        gameList.clear();
        StringBuilder sb = new StringBuilder();
        for (Long id : data.getGames().keySet()) 
        {
            String name = Database.getGame(id.toString()).getName();
            Long hours = data.getHours(id);
            if (hours != 0) {
                sb.append(name+"   ");
                if(hours != 1000000000 && hours != -1000000000) {
                    sb.append(Math.abs(data.getHours(id))+ " Hours Played");
                }
                sb.append("\n");
            }
            
        }
        gameList.appendText(sb.toString());
    }
}
