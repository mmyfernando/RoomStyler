import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Design2D extends JFrame {
    // UI Components from UI Designer
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
    private JPanel infoPanel;
    private JLabel dimentionLabel;
    private JLabel shapeLabel;

    // Design state
    private Room currentRoom;
    private Design currentDesign;
    private List<FurnitureItem> furnitureItems = new ArrayList<>();
    private FurnitureItem selectedItem = null;
    private Color currentFurnitureColor = Color.ORANGE;

    public Design2D() {
        // First, check if there's a current design in the DesignManager
        currentDesign = DesignManager.getCurrentDesign();

        // If there's a current design, use its room
        if (currentDesign != null) {
            currentRoom = currentDesign.getRoom();
            // Important: Load the furniture items from the design
            loadFurnitureFromDesign();
        } else {
            // No existing design, get the current room from RoomManager
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
        }

        // This is important: we need to wait until all UI components are created by the form
        SwingUtilities.invokeLater(() -> {
            // Update UI elements with current room information
            updateRoomInformation();

            // Configure room panel for drawing and interaction
            configureRoomPanel();

            // Configure furniture panel and populate combo box
            configureFurniturePanel();

            // Set up all the button event listeners
            setupEventListeners();

            // Force a repaint to make sure the room and furniture show up
            if (roomPanel != null) {
                roomPanel.repaint();
            }
        });
    }

    // New method to load furniture items from the design
    private void loadFurnitureFromDesign() {
        if (currentDesign != null) {
            // Clear the current furniture list
            furnitureItems.clear();

            // Add all items from the current design to our furniture list
            for (FurnitureItem item : currentDesign.getItems()) {
                furnitureItems.add(item);
            }
        }
    }

    private void updateRoomInformation() {
        // Update dimension and shape labels with current room information
        if (dimentionLabel != null && currentRoom != null) {
            dimentionLabel.setText("Room Dimensions: " +
                    currentRoom.getWidth() + "m x " +
                    currentRoom.getLength() + "m x " +
                    currentRoom.getHeight() + "m");
        }

        if (shapeLabel != null && currentRoom != null) {
            shapeLabel.setText("Room Shape: " + currentRoom.getShape());
        }
    }

    private void configureRoomPanel() {
        if (roomPanel != null) {
            // Make sure the room panel has a proper size
            roomPanel.setPreferredSize(new Dimension(600, 500));
            roomPanel.setBackground(Color.WHITE);

            // Instead of replacing the panel, add a component listener to it
            roomPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    roomPanel.repaint();
                }

                @Override
                public void componentResized(ComponentEvent e) {
                    roomPanel.repaint();
                }
            });

            // Override the panel's paint method to draw our custom content
            roomPanel.addContainerListener(new ContainerAdapter() {
                @Override
                public void componentAdded(ContainerEvent e) {
                    roomPanel.repaint();
                }
            });

            // Create a custom painting override
            roomPanel.addPropertyChangeListener("UI", evt -> roomPanel.repaint());

            // Important: Replace the UI delegate to enable custom painting without replacing the panel
            roomPanel.setUI(new javax.swing.plaf.PanelUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    super.paint(g, c);
                    drawRoom(g);
                    drawFurniture(g);
                }
            });

            // Setup mouse handling for the room panel
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
    }

    private void configureFurniturePanel() {
        if (furnitureTypeCombo != null) {
            // Clear and populate furniture type combo box
            furnitureTypeCombo.removeAllItems();
            furnitureTypeCombo.addItem("Chair");
            furnitureTypeCombo.addItem("Dining Table");
            furnitureTypeCombo.addItem("Side Table");
        }
    }

    private void setupEventListeners() {
        // Home button
        if (a2DDesignButton != null) {
            a2DDesignButton.addActionListener(e -> {
                Home home = new Home();
                Helper.navigateToFrame(Design2D.this, home, home.mainPanel, "Home", 1000, 800);
            });
        }

        // View 3D button
        if (view3DButton != null) {
            view3DButton.addActionListener(e -> {
                // Save the current design to be accessed by the 3D view
                DesignManager.setCurrentDesign(currentDesign);

                Design3D design3D = new Design3D();
                Helper.navigateToFrame(Design2D.this, design3D, design3D.mainPanel, "Design 3D", 1000, 800);
            });
        }

        // Save button
        if (saveButton != null) {
            saveButton.addActionListener(e -> {
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
            });
        }

        // Reset button
        if (resetButton != null) {
            resetButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(Design2D.this,
                        "Are you sure you want to clear all furniture items?",
                        "Reset Design",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    furnitureItems.clear();
                    currentDesign = new Design("New Design", currentRoom);
                    roomPanel.repaint();
                }
            });
        }

        // Add furniture button
        if (addFurnitureButton != null) {
            addFurnitureButton.addActionListener(e -> addSelectedFurniture());
        }

        // Change color button
        if (changeColorButton != null) {
            changeColorButton.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(
                        Design2D.this,
                        "Choose Furniture Color",
                        currentFurnitureColor);

                if (newColor != null) {
                    currentFurnitureColor = newColor;

                    // If an item is selected, change ONLY its color (not all items)
                    if (selectedItem != null) {
                        selectedItem.setColor(newColor);
                        roomPanel.repaint();
                    }
                    // If no item is selected, the color will be used for new furniture
                }
            });
        }
    }

    private void drawRoom(Graphics g) {
        if (roomPanel == null || currentRoom == null) return;

        Graphics2D g2d = (Graphics2D) g.create();

        try {
            int panelWidth = roomPanel.getWidth();
            int panelHeight = roomPanel.getHeight();

            if (panelWidth <= 0 || panelHeight <= 0) return;

            // Draw a border around the panel to make it visible
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(0, 0, panelWidth-1, panelHeight-1);

            // Calculate the size of the room to fit panel
            int maxSize = Math.min(panelWidth - 60, panelHeight - 60);

            if (maxSize <= 0) return;

            // Get the room's shape
            String roomShape = currentRoom.getShape().toLowerCase();

            // Draw the room based on its shape
            g2d.setColor(currentRoom.getFloorColor());

            switch (roomShape) {
                case "rectangle":
                    drawRectangularRoom(g2d, panelWidth, panelHeight, maxSize);
                    break;
                case "l-shape":
                case "l-shaped":
                case "l shape":
                case "l shaped":
                case "l":
                    drawLShapedRoom(g2d, panelWidth, panelHeight, maxSize);
                    break;
                case "t-shape":
                case "t-shaped":
                case "t shape":
                case "t shaped":
                case "t":
                    drawTShapedRoom(g2d, panelWidth, panelHeight, maxSize);
                    break;
                default:
                    // Default to square for unknown shapes
                    System.out.println("\n=== ROOM DEBUG INFO 1 ===");
                    int x = (panelWidth - maxSize) / 2;
                    int y = (panelHeight - maxSize) / 2;
                    g2d.fillRect(x, y, maxSize, maxSize);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x, y, maxSize, maxSize);
            }

            // Draw a coordinate system to help debug
            g2d.setColor(Color.GRAY);
            g2d.drawLine(panelWidth/2, 0, panelWidth/2, panelHeight);
            g2d.drawLine(0, panelHeight/2, panelWidth, panelHeight/2);
        } finally {
            g2d.dispose();
        }
    }

    private void drawRectangularRoom(Graphics2D g2d, int panelWidth, int panelHeight, int maxSize) {
        if (currentRoom == null) return;

        // Get actual dimensions from the room
        double roomWidth = currentRoom.getWidth();
        double roomLength = currentRoom.getLength();

        // Calculate the aspect ratio (width:length)
        double aspectRatio = roomWidth / roomLength;

        // Initialize display dimensions
        int displayWidth, displayHeight;

        // Determine which dimension should be set to maxSize
        if (aspectRatio > 1.0) {
            // Room is wider than long
            displayWidth = maxSize;
            displayHeight = (int)(maxSize / aspectRatio);
        } else {
            // Room is longer than wide
            displayHeight = maxSize;
            displayWidth = (int)(maxSize * aspectRatio);
        }

        // Make sure neither dimension exceeds panel constraints
        if (displayWidth > panelWidth - 60) {
            displayWidth = panelWidth - 60;
            displayHeight = (int)(displayWidth / aspectRatio);
        }

        if (displayHeight > panelHeight - 60) {
            displayHeight = panelHeight - 60;
            displayWidth = (int)(displayHeight * aspectRatio);
        }

        // Center the room in the panel
        int x = (panelWidth - displayWidth) / 2;
        int y = (panelHeight - displayHeight) / 2;

        // Draw the room
        g2d.fillRect(x, y, displayWidth, displayHeight);

        // Add a border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, displayWidth, displayHeight);

        // Add dimension labels for clarity
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();

        // Width label (horizontal)
        String widthLabel = String.format("%.1fm", roomWidth);
        int textWidth = fm.stringWidth(widthLabel);
        g2d.drawString(widthLabel, x + (displayWidth - textWidth) / 2, y - 5);

        // Length label (vertical)
        String lengthLabel = String.format("%.1fm", roomLength);
        g2d.drawString(lengthLabel, x - fm.stringWidth(lengthLabel) - 5, y + displayHeight / 2);
    }

    private void drawLShapedRoom(Graphics2D g2d, int panelWidth, int panelHeight, int maxSize) {
        int baseSize = (int)(maxSize * 0.8);
        int extension = (int)(maxSize * 0.4);

        int x = (panelWidth - baseSize) / 2;
        int y = (panelHeight - baseSize) / 2;

        // Draw L-shape using two rectangles
        // Main rectangle
        g2d.fillRect(x, y, baseSize, baseSize);

        // Extension rectangle (to form the L)
        g2d.fillRect(x - extension, y + baseSize - extension, extension, extension);

        // Draw border
        g2d.setColor(Color.BLACK);
        // Draw the outer L shape border
        g2d.drawRect(x, y, baseSize, baseSize);
        g2d.drawRect(x - extension, y + baseSize - extension, extension, extension);

        // Draw the inner corner edge (to complete the L shape)
        g2d.drawLine(x, y + baseSize - extension, x, y + baseSize);
        g2d.drawLine(x, y + baseSize - extension, x - extension, y + baseSize - extension);
    }

    private void drawTShapedRoom(Graphics2D g2d, int panelWidth, int panelHeight, int maxSize) {
        int baseWidth = (int)(maxSize * 0.8);
        int baseHeight = (int)(maxSize * 0.6);
        int topWidth = (int)(maxSize * 0.4);
        int topHeight = (int)(maxSize * 0.4);

        int baseX = (panelWidth - baseWidth) / 2;
        int baseY = (panelHeight - baseHeight + topHeight) / 2;
        int topX = (panelWidth - topWidth) / 2;
        int topY = baseY - topHeight;

        // Draw T-shape using two rectangles
        // Horizontal bar of T
        g2d.fillRect(baseX, baseY, baseWidth, baseHeight);

        // Vertical bar of T
        g2d.fillRect(topX, topY, topWidth, topHeight);

        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(baseX, baseY, baseWidth, baseHeight);
        g2d.drawRect(topX, topY, topWidth, topHeight);

        // Draw connecting lines to complete the shape
        g2d.drawLine(topX, baseY, baseX, baseY);
        g2d.drawLine(topX + topWidth, baseY, baseX + baseWidth, baseY);
    }

    private void drawFurniture(Graphics g) {
        // Make sure we have furniture to draw
        if (furnitureItems == null || furnitureItems.isEmpty()) {
            System.out.println("No furniture items to draw");
            return;
        }

        // Iterate through each furniture item and draw it
        for (FurnitureItem item : furnitureItems) {
            if (item == null) continue;

            Point pos = item.getPosition();
            Dimension size = item.getSize();

            if (pos == null || size == null) continue;

            // Draw the furniture based on its type
            switch (item.getType()) {
                case CHAIR:
                    drawChair(g, item);
                    break;
                case DINING_TABLE:
                    drawDiningTable(g, item);
                    break;
                case SIDE_TABLE:
                    drawSideTable(g, item);
                    break;
                default:
                    // Default drawing for unknown types
                    g.setColor(item.getColor());
                    g.fillRect(pos.x, pos.y, size.width, size.height);
            }

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

    // Draw a chair with a more realistic shape
    private void drawChair(Graphics g, FurnitureItem item) {
        Point pos = item.getPosition();
        Dimension size = item.getSize();
        Color color = item.getColor();

        // Use darker color for chair legs and back
        Color darkerColor = new Color(
                Math.max((int)(color.getRed() * 0.7), 0),
                Math.max((int)(color.getGreen() * 0.7), 0),
                Math.max((int)(color.getBlue() * 0.7), 0)
        );

        // Chair seat
        g.setColor(color);
        g.fillRect(pos.x, pos.y, size.width, size.height);

        // Chair back
        int backHeight = (int)(size.height * 0.8);
        g.setColor(darkerColor);
        g.fillRect(pos.x, pos.y - backHeight, size.width/3, backHeight);

        // Chair legs - just small rectangles at corners
        int legWidth = size.width/8;
        int legHeight = size.height/4;

        g.setColor(darkerColor);
        // Front legs
        g.fillRect(pos.x, pos.y + size.height, legWidth, legHeight);
        g.fillRect(pos.x + size.width - legWidth, pos.y + size.height, legWidth, legHeight);

        // Back legs - extend from the back
        g.fillRect(pos.x, pos.y + size.height, legWidth, legHeight);
        g.fillRect(pos.x + size.width/3 - legWidth, pos.y + size.height, legWidth, legHeight);
    }

    // Draw a dining table with more details
    private void drawDiningTable(Graphics g, FurnitureItem item) {
        Point pos = item.getPosition();
        Dimension size = item.getSize();
        Color color = item.getColor();

        // Darker color for table legs
        Color darkerColor = new Color(
                Math.max((int)(color.getRed() * 0.7), 0),
                Math.max((int)(color.getGreen() * 0.7), 0),
                Math.max((int)(color.getBlue() * 0.7), 0)
        );

        // Tabletop
        g.setColor(color);
        g.fillRect(pos.x, pos.y, size.width, size.height);
        g.setColor(Color.BLACK);
        g.drawRect(pos.x, pos.y, size.width, size.height);

        // Table legs
        int legWidth = size.width/10;
        int legHeight = size.height/3;

        g.setColor(darkerColor);
        // Draw four legs at corners
        g.fillRect(pos.x + legWidth, pos.y + size.height, legWidth, legHeight);
        g.fillRect(pos.x + size.width - 2*legWidth, pos.y + size.height, legWidth, legHeight);
        g.fillRect(pos.x + legWidth, pos.y + size.height, legWidth, legHeight);
        g.fillRect(pos.x + size.width - 2*legWidth, pos.y + size.height, legWidth, legHeight);
    }

    // Draw a side table with details
    private void drawSideTable(Graphics g, FurnitureItem item) {
        Point pos = item.getPosition();
        Dimension size = item.getSize();
        Color color = item.getColor();

        // Tabletop
        g.setColor(color);
        g.fillRect(pos.x, pos.y, size.width, size.height);
        g.setColor(Color.BLACK);
        g.drawRect(pos.x, pos.y, size.width, size.height);

        // Central leg or support
        int legWidth = size.width/3;
        int legHeight = size.height/3;

        g.setColor(new Color(
                Math.max((int)(color.getRed() * 0.8), 0),
                Math.max((int)(color.getGreen() * 0.8), 0),
                Math.max((int)(color.getBlue() * 0.8), 0)
        ));

        // Central support
        int centerX = pos.x + (size.width - legWidth)/2;
        g.fillRect(centerX, pos.y + size.height, legWidth, legHeight);
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