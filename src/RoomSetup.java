import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomSetup extends JFrame{

    JPanel mainPanel;
    private JButton roomSetupButton;

    public RoomSetup() {
        roomSetupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Home home = new Home();
                Helper.navigateToFrame(RoomSetup.this, home, home.mainPanel, "Login", 1000, 800);
            }
        });
    }
}
