//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class SearchApp extends JFrame{
//    private JPanel LoginPanel;
//    private JLabel LoginLabel;
//    private JButton yesButton;
//    private JButton noButton;
//
//    public SearchApp(){
//        setTitle("CS310 Project");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(500,500);
//        setLocationRelativeTo(null);
//        setVisible(true);
//        setContentPane(LoginPanel);
//
//        //com.searchengine.GameSearchApplication.BuildUserSearch().GameSearch();
//
//        yesButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (e.getSource() == yesButton){
//                    dispose();
//                    IDSearchApp loginWindow = new IDSearchApp();
//                }
//            }
//        });
//        noButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (e.getSource() == noButton){
//                    dispose();
//                    NoButtonSearchApp noSearchApp = new NoButtonSearchApp();
//                }
//            }
//        });
//    }
//
//    public static void main(String[] args) {
//        new SearchApp();
//    }
//}

package UI;

import javax.swing.JOptionPane;

import com.searchengine.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SearchApp extends Application 
{
    public static SearchApp instance;
    private static GameSearchApplication search;
    private static Stage stage;

    public SearchApp() 
    {
        instance = this;
        search = new GameSearchApplication();
    }

    public static void launchApp() 
    {
        try {
            launch(SearchApp.class);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,"REVISING SEARCH");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws RuntimeException
    {
        if (Database.size() < 1) 
        {
            throw new RuntimeException();
        }
        stage = primaryStage;
        stage.setTitle("GAME SEARCH UI");
        stage.setScene(new Scene(new MainWindow().getRoot())); 
        stage.show();
    }

    public static void display(WINDOW window) {
        try {
            Stage source = (Stage) stage.getScene().getWindow();
            source.setScene(new Scene(window.getRoot()));
        } catch (NullPointerException e) {
            System.out.println("NULL SCENE");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static GameSearchApplication getSearchInstance() 
    {
        return search;
    }
}