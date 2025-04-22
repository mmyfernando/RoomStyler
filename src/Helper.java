import javax.swing.*;

public class Helper {
    public static void navigateToFrame(JFrame currentFrame, JFrame newFrame,
                                       JPanel contentPane, String title,
                                       int width, int height) {

        newFrame.setContentPane(contentPane);
        newFrame.setTitle(title);
        newFrame.setSize(width, height);
        newFrame.setVisible(true);
        currentFrame.dispose();
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}