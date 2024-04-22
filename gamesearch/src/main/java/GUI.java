import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame{
    private JPanel LoginPanel;
    private JLabel LoginLabel;
    private JButton yesButton;
    private JButton noButton;

    public GUI(){
        setTitle("CS310 Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(LoginPanel);

        //GameSearchApplication.BuildUserSearch().GameSearch();

        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == yesButton){
                    dispose();
                    IDGUI loginWindow = new IDGUI();
                }
            }
        });
    }

    public static void main(String[] args) {
        new GUI();
    }
}
