package UI;

import java.io.IOException;
import javax.swing.JOptionPane;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class LoginWindow extends WINDOW {

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
    public void search() 
    {
        SearchApp.display(new GameWindow());
    }

    @FXML
    public void onLogin() 
    {
        try {
            SearchApp.login(steamIDInput.getText());   // builds userdata from steamid
            SearchApp.display(new GameWindow());
        } catch (SteamApiException sae) {
            JOptionPane.showMessageDialog(null,sae.getMessage());
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,io.getMessage());
        } finally {
            steamIDInput.clear();
        }
    }

    @FXML
    public void exit() 
    {
        Platform.exit();
    }
}