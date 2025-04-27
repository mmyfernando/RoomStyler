import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Login login = new Login();
        login.setContentPane(login.mainPanel);
        login.setTitle("Login");
        login.setSize(1000, 800);
        login.setVisible(true);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}