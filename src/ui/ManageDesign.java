import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageDesign extends JFrame {
     JPanel mainPanel;
     private JButton manageDesignsButton;
     private JPanel listPanel;

     public ManageDesign() {
          // Set layout for list panel
          listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

          // Setup back button
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
          // Create a panel for this design
          JPanel designPanel = new JPanel();
          designPanel.setLayout(new BorderLayout());
          designPanel.setBorder(BorderFactory.createCompoundBorder(
                  BorderFactory.createEmptyBorder(5, 5, 5, 5),
                  BorderFactory.createLineBorder(Color.GRAY)
          ));

          // Design info panel
          JPanel infoPanel = new JPanel(new GridLayout(3, 1));

          Room room = design.getRoom();
          String roomInfo = String.format("Room: %s (%.1f x %.1f x %.1f)",
                  room.getShape(),
                  room.getWidth(),
                  room.getLength(),
                  room.getHeight()
          );

          JLabel nameLabel = new JLabel("Name: " + design.getName());
          nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

          JLabel dateLabel = new JLabel("Modified: " + design.getLastModifiedDate().toString());
          dateLabel.setFont(new Font("Arial", Font.ITALIC, 12));

          JLabel roomLabel = new JLabel(roomInfo);

          infoPanel.add(nameLabel);
          infoPanel.add(roomLabel);
          infoPanel.add(dateLabel);

          // Buttons panel
          JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 0));

          JButton viewButton = new JButton("View");
          viewButton.addActionListener(e -> {
               DesignManager.setCurrentDesign(design);
               Design2D design2D = new Design2D();
               Helper.navigateToFrame(this, design2D, design2D.mainPanel, "Design 2D", 1000, 800);
          });

          JButton view3DButton = new JButton("View 3D");
          view3DButton.addActionListener(e -> {
               DesignManager.setCurrentDesign(design);
               Design3D design3D = new Design3D();
               Helper.navigateToFrame(this, design3D, design3D.mainPanel, "Design 3D", 1000, 800);
          });

          JButton deleteButton = new JButton("Delete");
          deleteButton.setBackground(new Color(220, 53, 69)); // Red color
          deleteButton.setForeground(Color.WHITE);
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

          buttonPanel.add(viewButton);
          buttonPanel.add(view3DButton);
          buttonPanel.add(deleteButton);

          // Add panels to design panel
          designPanel.add(infoPanel, BorderLayout.CENTER);
          designPanel.add(buttonPanel, BorderLayout.SOUTH);

          // Add design panel to list
          JPanel wrapperPanel = new JPanel(new BorderLayout());
          wrapperPanel.add(designPanel, BorderLayout.CENTER);
          wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

          listPanel.add(wrapperPanel);

          // Add a separator
          listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
     }
}