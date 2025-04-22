import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Design2D extends JFrame {

    public JPanel mainPanel;
    private JButton a2DDesignButton;
    private JButton resetButton;
    private JButton view3DButton;
    private JButton saveButton;

    public Design2D() {
        a2DDesignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Home home = new Home();
                Helper.navigateToFrame(Design2D.this, home, home.mainPanel, "Home", 1000, 800);
            }
        });
        view3DButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Design3D design3D = new Design3D();
                Helper.navigateToFrame(Design2D.this, design3D, design3D.mainPanel, "Design 3D", 1000, 800);
            }
        });
    }
}
