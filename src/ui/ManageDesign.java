import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

public class ManageDesign extends JFrame {
     JPanel mainPanel;
     private JButton manageDesignsButton;
     private JPanel listPanel;

     // Define colors
     private final Color DARK_GREEN = new Color(0, 80, 0);
     private final Color LIGHT_GRAY = new Color(178, 191, 173);
     private final Color EDIT_BUTTON_COLOR = new Color(0, 80, 0);
     private final Color VIEW_3D_BUTTON_COLOR = new Color(153, 76, 0);
     private final Color DELETE_BUTTON_COLOR = new Color(100, 0, 0);

     public ManageDesign() {
          // Set layout for list panel
          listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

          // Style back button
          manageDesignsButton.setIcon(createBackIcon());
          manageDesignsButton.setText("Manage Designs");
          manageDesignsButton.setFont(new Font("Arial", Font.BOLD, 18));
          manageDesignsButton.setForeground(DARK_GREEN);
          manageDesignsButton.setHorizontalAlignment(SwingConstants.LEFT);
          manageDesignsButton.setBorderPainted(false);
          manageDesignsButton.setContentAreaFilled(false);
          manageDesignsButton.setFocusPainted(false);

          // Setup back button action
          manageDesignsButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                    Home home = new Home();
                    Helper.navigateToFrame(ManageDesign.this, home, home.mainPanel, "Home", 1000, 800);
               }
          });

          // Load the saved designs
          loadSavedDesigns();
     }

     private ImageIcon createBackIcon() {
          // Create a simple back arrow icon - you can replace with an actual image file
          int size = 25;
          BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
          Graphics2D g = image.createGraphics();
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setColor(DARK_GREEN);

          int[] xPoints = {size - 5, 5, size - 5};
          int[] yPoints = {5, size / 2, size - 5};
          g.fillPolygon(xPoints, yPoints, 3);

          g.dispose();
          return new ImageIcon(image);
     }

     private void loadSavedDesigns() {
          // Get all saved designs
          List<Design> designs = DesignManager.getSavedDesigns();

          if (designs.isEmpty()) {
               // Display message if no designs found
               JLabel noDesignsLabel = new JLabel("No saved designs found.");
               noDesignsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
               noDesignsLabel.setFont(new Font("Arial", Font.BOLD, 14));
               noDesignsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
               listPanel.add(noDesignsLabel);
          } else {
               // Add a title
               JLabel titleLabel = new JLabel("Saved Designs");
               titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
               titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
               titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
               listPanel.add(titleLabel);

               // Add each design to the list
               for (Design design : designs) {
                    addDesignPanel(design);
               }
          }
     }

     private void addDesignPanel(Design design) {
          // Create a panel for this design with gray background
          JPanel designPanel = new JPanel();
          designPanel.setLayout(new BorderLayout(10, 0));
          designPanel.setBackground(LIGHT_GRAY);
          designPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

          // Design info panel on the left
          JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 5));
          infoPanel.setBackground(LIGHT_GRAY);

          Room room = design.getRoom();
          String roomInfo = String.format("Room : Rectangle( %.1fm x %.1fm x %.1fm )",
                  room.getWidth(),
                  room.getLength(),
                  room.getHeight()
          );

          JLabel nameLabel = new JLabel("Name : " + design.getName());
          nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

          JLabel roomLabel = new JLabel(roomInfo);
          roomLabel.setFont(new Font("Arial", Font.PLAIN, 14));

          String formattedDate = formatDate(design.getLastModifiedDate());
          JLabel dateLabel = new JLabel("Modified : " + formattedDate);
          dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));

          infoPanel.add(nameLabel);
          infoPanel.add(roomLabel);
          infoPanel.add(dateLabel);

          // Buttons panel on the right with vertical layout
          JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
          buttonPanel.setBackground(LIGHT_GRAY);
          buttonPanel.setPreferredSize(new Dimension(150, 120)); // Fixed width for buttons

          // Edit button (replace View button)
          JButton editButton = createStyledButton("Edit", EDIT_BUTTON_COLOR, Color.WHITE);
          editButton.addActionListener(e -> {
               DesignManager.setCurrentDesign(design);
               Design2D design2D = new Design2D();
               Helper.navigateToFrame(this, design2D, design2D.mainPanel, "Design 2D", 1000, 800);
          });

          // View 3D button with brown background
          JButton view3DButton = createStyledButton("View 3D", VIEW_3D_BUTTON_COLOR, Color.WHITE);
          view3DButton.addActionListener(e -> {
               DesignManager.setCurrentDesign(design);
               Design3D design3D = new Design3D();
               Helper.navigateToFrame(this, design3D, design3D.mainPanel, "Design 3D", 1000, 800);
          });

          // Delete button with dark red background
          JButton deleteButton = createStyledButton("Delete", DELETE_BUTTON_COLOR, Color.WHITE);
          deleteButton.addActionListener(e -> {
               int result = JOptionPane.showConfirmDialog(
                       this,
                       "Are you sure you want to delete this design?",
                       "Delete Design",
                       JOptionPane.YES_NO_OPTION,
                       JOptionPane.WARNING_MESSAGE
               );

               if (result == JOptionPane.YES_OPTION) {
                    DesignManager.deleteDesign(design.getName());

                    // Refresh the list
                    listPanel.removeAll();
                    loadSavedDesigns();
                    listPanel.revalidate();
                    listPanel.repaint();
               }
          });

          buttonPanel.add(editButton);
          buttonPanel.add(view3DButton);
          buttonPanel.add(deleteButton);

          // Add panels to design panel
          designPanel.add(infoPanel, BorderLayout.CENTER);
          designPanel.add(buttonPanel, BorderLayout.EAST);

          // Add design panel to list with vertical spacing
          JPanel wrapperPanel = new JPanel(new BorderLayout());
          wrapperPanel.add(designPanel, BorderLayout.CENTER);
          wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

          listPanel.add(wrapperPanel);

          // Add a separator
          listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
     }

     private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
          JButton button = new JButton(text);
          button.setBackground(bgColor);
          button.setForeground(fgColor);
          button.setFont(new Font("Arial", Font.BOLD, 14));
          button.setFocusPainted(false);
          button.setBorderPainted(false);
          button.setPreferredSize(new Dimension(150, 35));
          return button;
     }

     private String formatDate(java.util.Date date) {
          // Format date to match the design (e.g., "15 APR, 2025")
          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM, yyyy");
          return sdf.format(date).toUpperCase();
     }
}