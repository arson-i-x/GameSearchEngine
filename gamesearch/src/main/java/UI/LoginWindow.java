package UI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.searchengine.Game;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginWindow extends WINDOW implements Initializable {

    private final String imagePath = (WINDOW.class.getResource("helpImage.jpg").toString());

    private final Image helpImage = new Image(imagePath);

    @FXML
    TextField steamIDInput;

    @FXML
    ImageView image;

    public LoginWindow() 
    {
        open("LoginWindow.fxml");
    }

    @FXML
    public void onLogin() 
    {
        try {
            // builds userdata from steamid
            SearchApp.getSearchInstance().BuildUserSearch(steamIDInput.getText());

            // tells user library is private but does not exit
            if (SearchApp.getSearchInstance().getUserData().isEmpty()) {
                JOptionPane.showMessageDialog(null,"Please set Steam Library to public.");
            }
            
            // loads first game into window and displays it

            Game GameToPresent = SearchApp.getSearchInstance().nextGame();
            
            SearchApp.display(GameWindow.presentGame(GameToPresent, (long)1));
            

        } catch (SteamApiException sae) {
            JOptionPane.showMessageDialog(null,"API ERROR: ENSURE ID IS CORRECT");
            steamIDInput.clear();
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,"PLEASE ENTER YOUR STEAM ID" + "\n" + io.getMessage());
            steamIDInput.clear();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.image.setImage(helpImage);
    }
}
