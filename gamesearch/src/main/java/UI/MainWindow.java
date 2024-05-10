package UI;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainWindow extends WINDOW {
    
    public MainWindow() 
    {
        open("MainWindow.fxml");
    }

    @FXML
    public void exit() 
    {
        Platform.exit();
    }


    @FXML
    public void onLogin() 
    {
        SearchApp.display(new LoginWindow());
    }

    @FXML
    public void notLogin() 
    {
        SearchApp.display(new SearchWindow());
    }
}
