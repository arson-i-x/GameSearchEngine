package UI;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.searchengine.Game;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginWindow extends WINDOW {

    @FXML
    TextField steamIDInput;

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
            SearchApp.display(GameWindow.presentGame(SearchApp.getSearchInstance().nextGame()));
            

        } catch (SteamApiException sae) {
            JOptionPane.showMessageDialog(null,"API ERROR: ENSURE ID IS CORRECT");
            steamIDInput.clear();
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,"PLEASE ENTER YOUR STEAM ID" + "\n" + io.getMessage());
            steamIDInput.clear();
        }
    }
}
