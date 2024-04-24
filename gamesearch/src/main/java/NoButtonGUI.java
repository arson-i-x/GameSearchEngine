import javax.swing.*;

public class NoButtonGUI extends JFrame{
    private JLabel AddLabel;
    private JTextField gameInput;
    private JTextArea yourGamesArea;
    private JTextArea RecomendedOutput;
    private JPanel NoButtonGUI;

    public NoButtonGUI(){
        setTitle("CS310 Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setVisible(true);
        setContentPane(NoButtonGUI);
    }

    public static void main(String[] args) {
        new NoButtonGUI();
    }
}
