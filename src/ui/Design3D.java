import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Design3D extends JFrame {
    public JPanel mainPanel;
    private JButton titleBtn;
    private JButton toggleButton;
    private JButton view2DButton;
    private JButton saveButton;
    private JPanel renderPanel;
    private JLabel roomLabel;
    private JLabel shapeLabel;
    private JButton zoomInBtn;
    private JButton zoomOutBtn;
    private JButton resetZoomButton;

    // Design data
    private Design currentDesign;
    private double scale = 1.0;
    private double rotationX = 30.0;
    private double rotationY = 30.0;
    private Point lastMousePos;

    // Lighting settings for 3D effect
    private boolean lightingEnabled = true;

    public Design3D() {
        // Get the current design
        currentDesign = DesignManager.getCurrentDesign();

        if (currentDesign == null) {
            // If no design exists, show an error and return to 2D design
            JOptionPane.showMessageDialog(this,
                    "No design found. Please create a 2D design first.",
                    "Design Required",
                    JOptionPane.ERROR_MESSAGE);
            Design2D design2D = new Design2D();
            Helper.navigateToFrame(this, design2D, design2D.mainPanel, "Design 2D", 1000, 800);
            return;
        }

        // This is critical - use invokeLater to ensure UI components are initialized
        SwingUtilities.invokeLater(() -> {
            // Update room information in labels
            updateRoomLabels();
            // Configure the rendering panel
            configureRenderPanel();
            // Setup button actions
            setupEventListeners();
        });
        zoomInBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scale *= 1.2;
                renderPanel.repaint();
            }
        });
        zoomOutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scale *= 0.8;
                renderPanel.repaint();
            }
        });
        resetZoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scale = 1.0;
                renderPanel.repaint();
            }
        });
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lightingEnabled = !lightingEnabled;
                renderPanel.repaint();
            }
        });
    }

    private void updateRoomLabels() {
        if (roomLabel != null && currentDesign != null && currentDesign.getRoom() != null) {
            Room room = currentDesign.getRoom();
            roomLabel.setText("Room: " + room.getWidth() + "m x " +
                    room.getLength() + "m x " + room.getHeight() + "m");
        }
        if (shapeLabel != null && currentDesign != null && currentDesign.getRoom() != null) {
            shapeLabel.setText("Shape: " + currentDesign.getRoom().getShape());
        }
    }

    private void configureRenderPanel() {
        if (renderPanel != null) {
            renderPanel.setBackground(Color.WHITE);

            // Create a custom rendering panel to override painting
            JPanel customRenderPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (currentDesign != null) {
                        Graphics2D g2d = (Graphics2D) g;
                        // Enable anti-aliasing for smoother lines
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                                RenderingHints.VALUE_STROKE_PURE);

                        // Draw the 3D room and furniture
                        draw3DRoom(g2d);
                    }
                }
            };

            // Set size and layout for the custom panel
            customRenderPanel.setBackground(Color.WHITE);
            customRenderPanel.setPreferredSize(new Dimension(800, 600));

            // Add mouse listeners for rotation
            customRenderPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastMousePos = e.getPoint();
                }
            });

            customRenderPanel.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (lastMousePos != null) {
                        int dx = e.getX() - lastMousePos.x;
                        int dy = e.getY() - lastMousePos.y;
                        rotationY += dx * 0.5;
                        rotationX += dy * 0.5;
                        // Constrain rotation X to prevent weird views
                        rotationX = Math.max(0, Math.min(90, rotationX));
                        lastMousePos = e.getPoint();
                        customRenderPanel.repaint();
                    }
                }
            });

            // Clear the render panel and add our custom panel
            renderPanel.removeAll();
            renderPanel.setLayout(new BorderLayout());
            renderPanel.add(customRenderPanel, BorderLayout.CENTER);
            renderPanel.revalidate();
            renderPanel.repaint();
        }
    }

    private void setupEventListeners() {
        // Home button
        if (titleBtn != null) {
            titleBtn.addActionListener(e -> {
                Home home = new Home();
                Helper.navigateToFrame(Design3D.this, home, home.mainPanel, "Home", 1000, 800);
            });
        }

        // View 2D button
        if (view2DButton != null) {
            view2DButton.addActionListener(e -> {
                Design2D design2D = new Design2D();
                Helper.navigateToFrame(Design3D.this, design2D, design2D.mainPanel, "Design 2D", 1000, 800);
            });
        }

        // Save button
        if (saveButton != null) {
            saveButton.addActionListener(e -> {
                String designName = JOptionPane.showInputDialog(Design3D.this,
                        "Enter a name for this design:",
                        "Save Design",
                        JOptionPane.PLAIN_MESSAGE);

                if (designName != null && !designName.trim().isEmpty()) {
                    currentDesign.setName(designName);
                    DesignManager.saveDesign(currentDesign);
                    JOptionPane.showMessageDialog(Design3D.this,
                            "Design saved successfully!",
                            "Save Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }

    private void draw3DRoom(Graphics2D g2d) {
        if (currentDesign == null || renderPanel == null) return;

        Room room = currentDesign.getRoom();
        if (room == null) return;

        // Calculate room dimensions
        double width = room.getWidth() * 50 * scale; // Scale to pixels
        double length = room.getLength() * 50 * scale;
        double height = room.getHeight() * 50 * scale;

        // Calculate center point of panel
        int centerX = renderPanel.getWidth() / 2;
        int centerY = renderPanel.getHeight() / 2;

        // Apply rotation
        g2d.translate(centerX, centerY);

        // Draw a sky/background gradient
        UIRenderingUtil.drawBackground(g2d, centerX, centerY);

        // Draw room based on shape
        String roomShape = room.getShape().toLowerCase();
        if (roomShape.contains("l shape")) {
            RoomRenderingUtil.drawLShapedRoom3D(g2d, room, width, length, height,
                    rotationX, rotationY, lightingEnabled);
        } else if (roomShape.contains("t shape")) {
            RoomRenderingUtil.drawTShapedRoom3D(g2d, room, width, length, height,
                    rotationX, rotationY, lightingEnabled);
        } else {
            // Default rectangular room
            RoomRenderingUtil.drawRectangularRoom3D(g2d, room, width, length, height,
                    rotationX, rotationY, lightingEnabled);
        }

        // Draw furniture
        for (FurnitureItem item : currentDesign.getItems()) {
            switch (item.getType()) {
                case CHAIR:
                    FurnitureRenderingUtil.drawChair3D(g2d, item, scale, rotationX, rotationY, lightingEnabled);
                    break;
                case DINING_TABLE:
                    FurnitureRenderingUtil.drawDiningTable3D(g2d, item, scale, rotationX, rotationY, lightingEnabled);
                    break;
                case SIDE_TABLE:
                    FurnitureRenderingUtil.drawSideTable3D(g2d, item, scale, rotationX, rotationY, lightingEnabled);
                    break;
                default:
                    FurnitureRenderingUtil.drawGenericFurniture3D(g2d, item, scale, rotationX, rotationY, lightingEnabled);
            }
        }

        // Draw a legend with room info and view controls
        UIRenderingUtil.drawLegend(g2d, room, centerX, centerY);

        // Draw compass to help with orientation (in bottom right corner)
        UIRenderingUtil.drawCompass(g2d, centerX - 50, centerY - 50, rotationY);
    }
}


