import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomSetup extends JFrame {

    JPanel mainPanel;
    private JButton roomSetupButton;
    private JButton resetButton;
    private JButton create2DRoomButton;
    private JComboBox shapeDropdown;
    private JTextField widthInput;
    private JTextField heightInput;
    private JTextField lengthInput;
    private JButton wallColorButton;
    private JButton floorColorButton1;

    // Default colors
    private Color wallColor = new Color(76, 153, 115); // Default green
    private Color floorColor = new Color(0, 153, 255); // Default blue

    public RoomSetup() {
        // Initialize dropdown if needed
        if (shapeDropdown.getItemCount() == 0) {
            shapeDropdown.addItem("Rectangle");
            shapeDropdown.addItem("L-Shaped");
            shapeDropdown.addItem("Square");
        }

        // Set default colors on buttons
        wallColorButton.setBackground(wallColor);
        floorColorButton1.setBackground(floorColor);

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
                if (validateInputs()) {
                    // Create a Room object with the user's specifications
                    String shape = shapeDropdown.getSelectedItem().toString();
                    double width = Double.parseDouble(widthInput.getText());
                    double length = Double.parseDouble(lengthInput.getText());
                    double height = Double.parseDouble(heightInput.getText());

                    Room room = new Room(shape, width, length, height, wallColor, floorColor);

                    // Store the room object for use in Design2D
                    RoomManager.setCurrentRoom(room);

                    Design2D design2D = new Design2D();
                    Helper.navigateToFrame(RoomSetup.this, design2D, design2D.mainPanel, "Design 2D", 1000, 800);
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset all fields to default
                shapeDropdown.setSelectedIndex(0);
                widthInput.setText("");
                heightInput.setText("");
                lengthInput.setText("");
                wallColor = new Color(76, 153, 115);
                floorColor = new Color(0, 153, 255);
                wallColorButton.setBackground(wallColor);
                floorColorButton1.setBackground(floorColor);
            }
        });

        wallColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = chooseColor(wallColorButton);
                if (newColor != null) {
                    wallColor = newColor;
                }
            }
        });

        floorColorButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = chooseColor(floorColorButton1);
                if (newColor != null) {
                    floorColor = newColor;
                }
            }
        });
    }

    private Color chooseColor(JButton button) {
        Color newColor = JColorChooser.showDialog(
                this,
                "Choose Color",
                button.getBackground());
        if (newColor != null) {
            button.setBackground(newColor);
        }
        return newColor;
    }

    private boolean validateInputs() {
        try {
            // Check if any field is empty
            if (widthInput.getText().isEmpty() ||
                    heightInput.getText().isEmpty() ||
                    lengthInput.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "All dimensions must be filled in",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Parse values to check if they're valid numbers
            double width = Double.parseDouble(widthInput.getText());
            double height = Double.parseDouble(heightInput.getText());
            double length = Double.parseDouble(lengthInput.getText());

            // Check if values are positive
            if (width <= 0 || height <= 0 || length <= 0) {
                JOptionPane.showMessageDialog(this,
                        "All dimensions must be positive numbers",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for dimensions",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}