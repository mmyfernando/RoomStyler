import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Design2D extends JFrame {
    public JPanel mainPanel;
    private JButton a2DDesignButton;
    private JButton resetButton;
    private JButton view3DButton;
    private JButton saveButton;
    private JPanel roomPanel;
    private JPanel furniturePanel;
    private JComboBox<String> furnitureTypeCombo;
    private JButton addFurnitureButton;
    private JButton changeColorButton;
    private JPanel titlePanel;

    // Design state
    private Room currentRoom;
    private Design currentDesign;
    private List<FurnitureItem> furnitureItems = new ArrayList<>();
    private FurnitureItem selectedItem = null;
    private Color currentFurnitureColor = Color.ORANGE;

    public Design2D() {
        // Get the current room from RoomManager
        currentRoom = RoomManager.getCurrentRoom();

        if (currentRoom == null) {
            // If no room exists, show an error and return to room setup
            JOptionPane.showMessageDialog(this,
                    "No room configuration found. Please set up a room first.",
                    "Room Required",
                    JOptionPane.ERROR_MESSAGE);

            RoomSetup roomSetup = new RoomSetup();
            Helper.navigateToFrame(this, roomSetup, roomSetup.mainPanel, "Room Setup", 1000, 800);
            return;
        }

        // Create a new design with the current room
        currentDesign = new Design("New Design", currentRoom);

        // Setup layout for the main panel
        setupLayout();

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
                // Save the current design to be accessed by the 3D view
                DesignManager.setCurrentDesign(currentDesign);

                Design3D design3D = new Design3D();
                Helper.navigateToFrame(Design2D.this, design3D, design3D.mainPanel, "Design 3D", 1000, 800);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String designName = JOptionPane.showInputDialog(Design2D.this,
                        "Enter a name for this design:",
                        "Save Design",
                        JOptionPane.PLAIN_MESSAGE);

                if (designName != null && !designName.trim().isEmpty()) {
                    currentDesign.setName(designName);
                    DesignManager.saveDesign(currentDesign);
                    JOptionPane.showMessageDialog(Design2D.this,
                            "Design saved successfully!",
                            "Save Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(Design2D.this,
                        "Are you sure you want to clear all furniture items?",
                        "Reset Design",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    furnitureItems.clear();
                    currentDesign = new Design("New Design", currentRoom);
                    roomPanel.repaint();
                }
            }
        });

        addFurnitureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSelectedFurniture();
            }
        });

        changeColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        Design2D.this,
                        "Choose Furniture Color",
                        currentFurnitureColor);

                if (newColor != null) {
                    currentFurnitureColor = newColor;

                    // If an item is selected, change its color
                    if (selectedItem != null) {
                        selectedItem.setColor(newColor);
                        roomPanel.repaint();
                    }
                }
            }
        });
    }

    private void setupLayout() {
        // Set the main panel layout
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(a2DDesignButton, BorderLayout.WEST);

        // Add room information at the top
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        JLabel dimensionsLabel = new JLabel("Room Dimensions: " +
                currentRoom.getWidth() + "m x " +
                currentRoom.getLength() + "m x " +
                currentRoom.getHeight() + "m");
        JLabel shapeLabel = new JLabel("Room Shape: " + currentRoom.getShape());

        infoPanel.add(dimensionsLabel);
        infoPanel.add(shapeLabel);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Setup room panel for drawing
        setupRoomPanel();

        // Setup furniture panel
        setupFurniturePanel();

        // Add room panel to center
        mainPanel.add(roomPanel, BorderLayout.CENTER);

        // Add furniture panel to east
        mainPanel.add(furniturePanel, BorderLayout.EAST);

        // Create bottom panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(view3DButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupRoomPanel() {
        // If roomPanel doesn't exist yet (in case it was created via GUI designer), create it
        if (roomPanel == null) {
            roomPanel = new JPanel();
        }

        roomPanel.setLayout(null); // Use absolute positioning

        roomPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRoom(g);
                drawFurniture(g);
            }
        };

        roomPanel.setBackground(Color.WHITE);

        // Add mouse listeners for interacting with furniture
        roomPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectFurnitureAt(e.getX(), e.getY());
            }
        });

        roomPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null) {
                    selectedItem.setPosition(new Point(e.getX(), e.getY()));
                    roomPanel.repaint();
                }
            }
        });
    }

    private void setupFurniturePanel() {
        // If furniturePanel doesn't exist yet, create it
        if (furniturePanel == null) {
            furniturePanel = new JPanel();
        }

        furniturePanel.setLayout(new BorderLayout());
        furniturePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Furniture Type:")
        ));

        // Initialize the furniture type combo box if it doesn't exist
        if (furnitureTypeCombo == null) {
            furnitureTypeCombo = new JComboBox<>();
        }

        // Clear and add items
        furnitureTypeCombo.removeAllItems();
        furnitureTypeCombo.addItem("Chair");
        furnitureTypeCombo.addItem("Dining Table");
        furnitureTypeCombo.addItem("Side Table");

        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 5));

        // Initialize buttons if they don't exist
        if (addFurnitureButton == null) {
            addFurnitureButton = new JButton("Add Furniture");
        }

        if (changeColorButton == null) {
            changeColorButton = new JButton("Change Color");
        }

        buttonPanel.add(changeColorButton);
        buttonPanel.add(addFurnitureButton);

        // Add components to furniture panel
        furniturePanel.add(furnitureTypeCombo, BorderLayout.NORTH);
        furniturePanel.add(new JScrollPane(new JPanel()), BorderLayout.CENTER); // Empty scroll panel for spacing
        furniturePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set preferred width
        furniturePanel.setPreferredSize(new Dimension(200, 500));
    }

    private void drawRoom(Graphics g) {
        int panelWidth = roomPanel.getWidth();
        int panelHeight = roomPanel.getHeight();

        // Calculate the size of the room to fit panel
        int roomWidth = Math.min(panelWidth - 40, panelHeight - 40);

        // Center the room in the panel
        int x = (panelWidth - roomWidth) / 2;
        int y = (panelHeight - roomWidth) / 2;

        // Draw the room (as a simple rectangle for now)
        g.setColor(currentRoom.getWallColor());
        g.fillRect(x, y, roomWidth, roomWidth);
    }

    private void drawFurniture(Graphics g) {
        for (FurnitureItem item : furnitureItems) {
            Point pos = item.getPosition();
            Dimension size = item.getSize();

            // Draw the furniture item
            g.setColor(item.getColor());
            g.fillRect(pos.x, pos.y, size.width, size.height);

            // Highlight selected item
            if (item == selectedItem) {
                g.setColor(Color.RED);
                g.drawRect(pos.x - 2, pos.y - 2, size.width + 4, size.height + 4);
            }

            // Draw label
            g.setColor(Color.BLACK);
            g.drawString(item.getType().getDisplayName(), pos.x, pos.y - 5);
        }
    }

    private void addSelectedFurniture() {
        String furnitureType = (String) furnitureTypeCombo.getSelectedItem();
        if (furnitureType == null) {
            return;
        }

        FurnitureType type = FurnitureType.valueOf(
                furnitureType.toUpperCase().replace(" ", "_"));

        // Default position in center of panel
        Point position = new Point(
                roomPanel.getWidth() / 2,
                roomPanel.getHeight() / 2);

        // Default size based on furniture type
        Dimension size;
        switch (type) {
            case CHAIR:
                size = new Dimension(40, 40);
                break;
            case DINING_TABLE:
                size = new Dimension(100, 60);
                break;
            case SIDE_TABLE:
                size = new Dimension(50, 50);
                break;
            default:
                size = new Dimension(60, 60);
        }

        FurnitureItem newItem = new FurnitureItem(type, size, position, currentFurnitureColor);
        furnitureItems.add(newItem);
        currentDesign.addItem(newItem);

        // Select the new item
        selectedItem = newItem;

        roomPanel.repaint();
    }

    private void selectFurnitureAt(int x, int y) {
        selectedItem = null;

        // Check if a furniture item was clicked
        for (FurnitureItem item : furnitureItems) {
            Point pos = item.getPosition();
            Dimension size = item.getSize();

            if (x >= pos.x && x <= pos.x + size.width &&
                    y >= pos.y && y <= pos.y + size.height) {
                selectedItem = item;
                break;
            }
        }

        roomPanel.repaint();
    }
}