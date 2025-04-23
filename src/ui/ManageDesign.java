import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageDesign extends JFrame {
     JPanel mainPanel;
     private JButton manageDesignsButton;

     public ManageDesign() {
          manageDesignsButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                    Home home = new Home();
                    Helper.navigateToFrame(ManageDesign.this, home, home.mainPanel, "Home", 1000, 800);
               }
          });
     }
}
