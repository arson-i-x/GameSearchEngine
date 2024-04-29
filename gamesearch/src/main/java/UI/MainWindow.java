package UI;

import javafx.fxml.FXML;


public class MainWindow extends WINDOW {
    
    public MainWindow() 
    {
        open("MainWindow.fxml");
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
