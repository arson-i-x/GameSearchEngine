import com.searchengine.IOController;
import com.searchengine.UserData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class IDGUI extends JFrame{
    private JLabel ID;
    private JTextField textField1;
    private JPanel IDWindow;
    private JTextArea GamesOutput;

    public IDGUI(){
        setTitle("CS310 Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(IDWindow);


        textField1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //String userInput = textField1.getText();
                GameSearchApplication.BuildUserSearch().GameSearch();
            }
        });


    }

    public static void main(String[] args) {
        new IDGUI();
        //76561198392275567
    }
}
