import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    JPanel mainPanel;
    private JLabel bg;
    private JButton loginButton;
    private JTextField userNameInput;
    private JPasswordField passwordInput;
    private JLabel errorLabel;

    public Login() {
        setTitle("Login");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateBackground();
            }
        });
        updateBackground();
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userNameInput.getText();
                String password = new String(passwordInput.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    errorLabel.setText("Username and password cannot be empty");
                    return;
                }

                if (UserManager.authenticateUser(username, password)) {
                    // Login successful
                    errorLabel.setText("");
                    Home home = new Home();
                    Helper.navigateToFrame(Login.this, home, home.mainPanel, "Home", 1000, 800);
                } else {
                    // Login failed
                    errorLabel.setText("Invalid username or password");
                    passwordInput.setText(""); // Clear password field
                }

                //skip login for testing
//                Home home = new Home();
//                Helper.navigateToFrame(Login.this, home, home.mainPanel, "Home", 1000, 800);
            }
        });
    }

    private void updateBackground() {
        int bgWidth = isMaximumSizeSet() ? getWidth()/2 : 500;
        bg.setBounds(0, 0, bgWidth, getHeight());
        setBackgroundImage(bgWidth, getHeight());
    }

    private void setBackgroundImage(int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon("img/bg2.jpg");
            Image scaledImage = originalIcon.getImage()
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);
            bg.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
        }
    }

    // Helper method to check if window is maximized
    public boolean isMaximumSizeSet() {
        return (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
    }
}