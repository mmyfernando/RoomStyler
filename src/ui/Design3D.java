import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;

public class Design3D extends JFrame {
    JPanel mainPanel;
    private JButton titleBtn;
    private JButton zoomButton;
    private JButton view2DButton;
    private JButton saveButton;

    // 3D rendering panel
    private JPanel renderPanel;

    // Design data
    private Design currentDesign;
    private double scale = 1.0;
    private double rotationX = 30.0;
    private double rotationY = 30.0;
    private Point lastMousePos;

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

        // Initialize the rendering panel
        initializeRenderPanel();

        // Create a panel for the buttons at the bottom
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(zoomButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(view2DButton);

        // Setup layout for mainPanel
        mainPanel.setLayout(new BorderLayout());

        // Add the rendering panel to the center
        mainPanel.add(renderPanel, BorderLayout.CENTER);

        // Add the button panel to the bottom
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Setup button actions
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

        zoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Zoom In", "Zoom Out", "Reset Zoom"};
                int choice = JOptionPane.showOptionDialog(
                        Design3D.this,
                        "Select zoom option:",
                        "Zoom Control",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                switch (choice) {
                    case 0: // Zoom In
                        scale *= 1.2;
                        break;
                    case 1: // Zoom Out
                        scale *= 0.8;
                        break;
                    case 2: // Reset
                        scale = 1.0;
                        rotationX = 30.0;
                        rotationY = 30.0;
                        break;
                }

                renderPanel.repaint();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });
    }

    private void initializeRenderPanel() {
        renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Enable anti-aliasing for smoother lines
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw the 3D room and furniture
                draw3DRoom(g2d);
            }
        };

        renderPanel.setBackground(Color.WHITE);
        renderPanel.setPreferredSize(new Dimension(800, 600)); // Set a preferred size

        // Add mouse listeners for rotation
        renderPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
            }
        });

        renderPanel.addMouseMotionListener(new MouseAdapter() {
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
                    renderPanel.repaint();
                }
            }
        });
    }

    private void draw3DRoom(Graphics2D g2d) {
        Room room = currentDesign.getRoom();

        // Calculate room dimensions
        double width = room.getWidth() * 50 * scale;  // Scale to pixels
        double length = room.getLength() * 50 * scale;
        double height = room.getHeight() * 50 * scale;

        // Calculate center point of panel
        int centerX = renderPanel.getWidth() / 2;
        int centerY = renderPanel.getHeight() / 2;

        // Apply rotation
        g2d.translate(centerX, centerY);

        // Draw room from an isometric perspective
        double[][] floorPoints = {
                {-width/2, -length/2, 0},
                {width/2, -length/2, 0},
                {width/2, length/2, 0},
                {-width/2, length/2, 0}
        };

        double[][] ceilingPoints = {
                {-width/2, -length/2, height},
                {width/2, -length/2, height},
                {width/2, length/2, height},
                {-width/2, length/2, height}
        };

        // Project 3D points to 2D based on rotation
        Point[] floor2D = new Point[4];
        Point[] ceiling2D = new Point[4];

        for (int i = 0; i < 4; i++) {
            floor2D[i] = projectPoint(floorPoints[i], rotationX, rotationY);
            ceiling2D[i] = projectPoint(ceilingPoints[i], rotationX, rotationY);
        }

        // Get wall color
        Color wallColor = room.getWallColor();
        if (wallColor == null) {
            wallColor = new Color(150, 150, 150); // Default gray if no color set
        }

        // Draw floor using wall color but slightly lighter
        g2d.setColor(lighter(wallColor));
        Path2D floorPath = new Path2D.Double();
        floorPath.moveTo(floor2D[0].x, floor2D[0].y);
        for (int i = 1; i < 4; i++) {
            floorPath.lineTo(floor2D[i].x, floor2D[i].y);
        }
        floorPath.closePath();
        g2d.fill(floorPath);
        g2d.setColor(wallColor);
        g2d.draw(floorPath);

        // Draw walls
        g2d.setColor(wallColor);
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;

            Path2D wallPath = new Path2D.Double();
            wallPath.moveTo(floor2D[i].x, floor2D[i].y);
            wallPath.lineTo(floor2D[next].x, floor2D[next].y);
            wallPath.lineTo(ceiling2D[next].x, ceiling2D[next].y);
            wallPath.lineTo(ceiling2D[i].x, ceiling2D[i].y);
            wallPath.closePath();

            g2d.fill(wallPath);
            g2d.setColor(darker(wallColor));
            g2d.draw(wallPath);
            g2d.setColor(wallColor);
        }

        // Draw ceiling
        g2d.setColor(lighter(wallColor));
        Path2D ceilingPath = new Path2D.Double();
        ceilingPath.moveTo(ceiling2D[0].x, ceiling2D[0].y);
        for (int i = 1; i < 4; i++) {
            ceilingPath.lineTo(ceiling2D[i].x, ceiling2D[i].y);
        }
        ceilingPath.closePath();
        g2d.fill(ceilingPath);
        g2d.setColor(wallColor);
        g2d.draw(ceilingPath);

        // Draw furniture
        for (FurnitureItem item : currentDesign.getItems()) {
            drawFurnitureItem(g2d, item);
        }

        // Draw legend
        g2d.setColor(Color.BLACK);
        g2d.drawString("Room: " + room.getWidth() + "m x " + room.getLength() + "m x " + room.getHeight() + "m", -centerX + 10, -centerY + 20);
        g2d.drawString("Shape: " + room.getShape(), -centerX + 10, -centerY + 40);
    }

    private void drawFurnitureItem(Graphics2D g2d, FurnitureItem item) {
        Point pos = item.getPosition();
        Dimension size = item.getSize();

        // Convert from 2D design coordinates to our 3D world
        // This is a simple mapping and might need adjustment based on your design
        double x = (pos.x - 400) * scale;  // Adjust based on your 2D coordinate system
        double y = (pos.y - 300) * scale;

        // Calculate 3D box for furniture
        double width = size.width * scale;
        double length = size.height * scale;
        double height = 30 * scale; // Default height for furniture

        // Adjust position to be relative to room center
        double[][] boxPoints = {
                // Bottom face
                {x - width/2, y - length/2, 0},
                {x + width/2, y - length/2, 0},
                {x + width/2, y + length/2, 0},
                {x - width/2, y + length/2, 0},
                // Top face
                {x - width/2, y - length/2, height},
                {x + width/2, y - length/2, height},
                {x + width/2, y + length/2, height},
                {x - width/2, y + length/2, height}
        };

        // Project points
        Point[] box2D = new Point[8];
        for (int i = 0; i < 8; i++) {
            box2D[i] = projectPoint(boxPoints[i], rotationX, rotationY);
        }

        // Get furniture color
        Color itemColor = item.getColor();
        if (itemColor == null) {
            itemColor = Color.orange; // Default color
        }

        // Draw bottom face
        g2d.setColor(itemColor);
        Path2D bottomFace = new Path2D.Double();
        bottomFace.moveTo(box2D[0].x, box2D[0].y);
        for (int i = 1; i < 4; i++) {
            bottomFace.lineTo(box2D[i].x, box2D[i].y);
        }
        bottomFace.closePath();
        g2d.fill(bottomFace);
        g2d.setColor(darker(itemColor));
        g2d.draw(bottomFace);

        // Draw sides
        g2d.setColor(darker(itemColor));
        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;

            Path2D sideFace = new Path2D.Double();
            sideFace.moveTo(box2D[i].x, box2D[i].y);
            sideFace.lineTo(box2D[next].x, box2D[next].y);
            sideFace.lineTo(box2D[next + 4].x, box2D[next + 4].y);
            sideFace.lineTo(box2D[i + 4].x, box2D[i + 4].y);
            sideFace.closePath();

            g2d.fill(sideFace);
            g2d.setColor(darker(darker(itemColor)));
            g2d.draw(sideFace);
            g2d.setColor(darker(itemColor));
        }

        // Draw top face
        g2d.setColor(itemColor);
        Path2D topFace = new Path2D.Double();
        topFace.moveTo(box2D[4].x, box2D[4].y);
        for (int i = 5; i < 8; i++) {
            topFace.lineTo(box2D[i].x, box2D[i].y);
        }
        topFace.closePath();
        g2d.fill(topFace);
        g2d.setColor(darker(itemColor));
        g2d.draw(topFace);

        // Draw label
        g2d.setColor(Color.BLACK);
        g2d.drawString(item.getType().getDisplayName(), box2D[4].x, box2D[4].y - 5);
    }

    private Point projectPoint(double[] point3D, double rotX, double rotY) {
        // Convert degrees to radians
        double radX = Math.toRadians(rotX);
        double radY = Math.toRadians(rotY);

        // Apply rotations
        double x = point3D[0];
        double y = point3D[1] * Math.cos(radX) - point3D[2] * Math.sin(radX);
        double z = point3D[1] * Math.sin(radX) + point3D[2] * Math.cos(radX);

        double x2 = x * Math.cos(radY) + z * Math.sin(radY);
        double z2 = -x * Math.sin(radY) + z * Math.cos(radY);

        // Simple projection (orthographic)
        return new Point((int)x2, (int)y);
    }

    // Helper methods for color manipulation
    private Color darker(Color c) {
        return new Color(
                Math.max((int)(c.getRed() * 0.7), 0),
                Math.max((int)(c.getGreen() * 0.7), 0),
                Math.max((int)(c.getBlue() * 0.7), 0)
        );
    }

    private Color lighter(Color c) {
        return new Color(
                Math.min((int)(c.getRed() * 1.3), 255),
                Math.min((int)(c.getGreen() * 1.3), 255),
                Math.min((int)(c.getBlue() * 1.3), 255)
        );
    }
}