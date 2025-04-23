import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {
    JPanel mainPanel;
    private JButton createNewDesignButton;
    private JButton manageDesignButton;
    private JButton logoutButton;

    public Home() {

        createNewDesignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RoomSetup roomSetup = new RoomSetup();
                Helper.navigateToFrame(Home.this, roomSetup, roomSetup.mainPanel, "Room setup", 1000, 800);
            }
        });
        manageDesignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageDesign manageDesign = new ManageDesign();
                Helper.navigateToFrame(Home.this, manageDesign, manageDesign.mainPanel, "Manage design", 1000, 800);
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login = new Login();
                Helper.navigateToFrame(Home.this, login, login.mainPanel, "Login", 1000, 800);
            }
        });
    }
}
