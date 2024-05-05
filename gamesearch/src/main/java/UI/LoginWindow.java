package UI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginWindow extends WINDOW implements Initializable {
    private final Image helpImage = new Image(WINDOW.class.getResourceAsStream("helpimage.jpg"));

    @FXML
    TextField steamIDInput;

    @FXML
    ImageView image;

    public LoginWindow() 
    {
        open("LoginWindow.fxml");
    }

    @FXML
    public void backButton() 
    {
        SearchApp.display(new MainWindow());
    }

    @FXML
    public void onLogin() 
    {
        try {
            SearchApp.getSearchInstance().login(steamIDInput.getText());   // builds userdata from steamid
            SearchApp.display(new GameWindow());
        } catch (SteamApiException sae) {
            JOptionPane.showMessageDialog(null,sae.getMessage());
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,io.getMessage());
        } finally {
            steamIDInput.clear();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.image.setImage(helpImage);
    }
}
