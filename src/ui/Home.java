import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {
    JPanel mainPanel;
    private JButton createNewDesignButton;
    private JButton manageDesignButton;
    private JButton logoutButton;
    private JLabel nameLabel;

    public Home() {

        User currentUser = UserManager.getCurrentUser();
        if (currentUser != null) {
            nameLabel.setText("Hi, " + currentUser.getUsername());
        } else {
            nameLabel.setText("Welcome, Guest");
        }


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
                int result = JOptionPane.showConfirmDialog(
                        Home.this,
                        "Are you sure you want to logout?",
                        "Confirm Logout",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                // Only proceed with logout if user confirms
                if (result == JOptionPane.YES_OPTION) {
                    // Clear the current user when logging out
                    UserManager.logout();

                    Login login = new Login();
                    Helper.navigateToFrame(Home.this, login, login.mainPanel, "Login", 1000, 800);
                }
            }
        });
    }
}
