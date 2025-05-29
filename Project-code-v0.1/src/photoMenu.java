import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class photoMenu extends JFrame {
    private JPanel Wrapper;
    private JList<String> photoList;
    private JButton confirmButton;
    private JScrollPane scrollPane;
    private JLabel photoPreview; // Add this to display the preview

    private String selectedPhoto;
    public static int user;

    public photoMenu(String currentUsername) {
        setContentPane(Wrapper);
        setTitle("Photo Menu");
        setSize(360, 740);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Populate list with .png files
        String[] photoFiles = getPhotoFileNames();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String fileName : photoFiles) {
            listModel.addElement(fileName);
        }
        photoList.setModel(listModel);

        // Listen for selection
        photoList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedPhoto = photoList.getSelectedValue();
                if (selectedPhoto != null) {
                    displayPhotoPreview(selectedPhoto);
                }
            }
        });

        // Confirm button action
        confirmButton.addActionListener(e -> {
            if (selectedPhoto != null) {
                String avatarUrl = "/Media/UserAvatars/" + selectedPhoto;
                updateUserAvatarUrl(currentUsername, avatarUrl);
                dispose();

                new ProfileInfoCompletion(user);

            } else {
                JOptionPane.showMessageDialog(this, "Please select a photo.");
            }
        });

        setVisible(true);
    }

    private String[] getPhotoFileNames() {
        File dir = new File("./Media/UserAvatars");
        String[] files = dir.list((d, name) -> name.toLowerCase().endsWith(".png"));
        return files != null ? files : new String[0];
    }

    private void displayPhotoPreview(String photoName) {
        try {
            File photoFile = new File("./Media/UserAvatars/" + photoName);
            BufferedImage image = ImageIO.read(photoFile);
            ImageIcon icon = new ImageIcon(image.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
            photoPreview.setIcon(icon);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading photo preview: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateUserAvatarUrl(String username, String avatarUrl) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
                 PreparedStatement ps = conn.prepareStatement("UPDATE user SET avatar_url = ? WHERE username = ?")) {
                ps.setString(1, avatarUrl);
                ps.setString(2, username);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated == 0) {
                    JOptionPane.showMessageDialog(this, "Failed to update avatar. User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Database driver not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}