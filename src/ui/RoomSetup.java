import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomSetup extends JFrame{

    JPanel mainPanel;
    private JButton roomSetupButton;
    private JButton resetButton;
    private JButton create2DRoomButton;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JButton chooseColorButton;
    private JButton chooseColorButton1;


    public RoomSetup() {

        roomSetupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Home home = new Home();
                Helper.navigateToFrame(RoomSetup.this, home, home.mainPanel, "Home", 1000, 800);
            }
        });

        create2DRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Design2D design2D = new Design2D();
                Helper.navigateToFrame(RoomSetup.this,design2D, design2D.mainPanel, "Design 2D", 1000, 800);
            }
        });
        chooseColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseColor(chooseColorButton);
            }
        });
        chooseColorButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseColor(chooseColorButton1);
            }
        });
    }

    private void chooseColor(JButton button) {
        Color newColor = JColorChooser.showDialog(
                this,
                "Choose Background Color",
                button.getBackground());
        if (newColor != null) {
            button.setBackground(newColor);
        }
    }


}
