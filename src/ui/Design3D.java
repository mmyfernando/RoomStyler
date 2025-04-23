import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Design3D extends JFrame {
    JPanel mainPanel;
    private JButton titleBtn;
    private JButton zoomButton;
    private JButton view2DButton;
    private JButton saveButton;
    private JButton a3DDesignButton;

    public Design3D() {
        titleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        titleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Home home = new Home();
                Helper.navigateToFrame(Design3D.this, home, home.mainPanel, "Home", 1000, 800);
            }
        });
        view2DButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Design2D design2D = new Design2D();
                Helper.navigateToFrame(Design3D.this, design2D, design2D.mainPanel, "Design 2D", 1000, 800);
            }
        });
    }
}
