import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditVenueListing extends JFrame {
    private JPanel Wrapper;
    private JPanel postPic;
    private JPanel information;
    private JTextField captionEdit;
    private JButton addPhotosButton;
    private JPanel photos;
    private JButton deletePhotosButton;
    private JPanel controlPanel;
    private JButton confirmButton;
    private JButton backButton;
    private int postId;
    private JLabel imageLabel;
    private List<String> currentPhotos;

    public EditVenueListing(int postId) {
        this.postId = postId;
        this.currentPhotos = new ArrayList<>();

        // Setup frame
        setTitle("Edit Venue Listing");
        setContentPane(Wrapper);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(375, 740);
        setLocationRelativeTo(null);
        setResizable(false);

        // Initialize image label
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        postPic.setLayout(new BorderLayout());
        postPic.add(imageLabel, BorderLayout.CENTER);

        // Load current caption and picture
        loadCaption();
        loadPicture();

        // Add action listeners
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCaption();
            }
        });

        addPhotosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPhotos();
            }
        });

        deletePhotosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePhotos();
            }
        });

        // Add action listeners
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void loadPicture() {
        DBManager dbManager = new DBManager();
        String picturesJson = dbManager.getPostPictures(postId);

        if (picturesJson != null && !picturesJson.isEmpty()) {
            currentPhotos = new ArrayList<>(Arrays.asList(picturesJson.split(",")));
            // Get the first picture from the list
            String firstPicture = currentPhotos.get(0).trim();

            try {
                // Construct path to the specific post folder
                String postsPath = "Project-code-v0.1/src/Media/Posts/" + postId;
                File postFolder = new File(postsPath);

                if (!postFolder.exists()) {
                    imageLabel.setText("Post folder not found: " + postsPath);
                    System.out.println("Post folder does not exist: " + postsPath);
                    return;
                }

                File imageFile = findImageInDirectory(postFolder, firstPicture);

                if (imageFile != null && imageFile.exists()) {
                    displayImage(imageFile);
                } else {
                    imageLabel.setText("Image not found: " + firstPicture);
                    System.out.println("Could not find image: " + firstPicture + " in folder: " + postsPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                imageLabel.setText("Error loading image");
            }
        } else {
            imageLabel.setText("No image available");
        }
    }

    private void displayImage(File imageFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageFile);

        // Get the panel's size
        int panelWidth = postPic.getWidth();
        int panelHeight = postPic.getHeight();

        // If panel size is 0 (not yet rendered), use default sizes
        if (panelWidth == 0) panelWidth = 360;  // default width
        if (panelHeight == 0) panelHeight = 360; // default height

        // Create scaled image that fits in the panel while maintaining aspect ratio
        Image scaledImage = getScaledImage(originalImage, panelWidth, panelHeight);

        // Update the label with the scaled image
        imageLabel.setIcon(new ImageIcon(scaledImage));
    }

    private void addPhotos() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            // Create directory if it doesn't exist
            String dirPath = "Project-code-v0.1/src/Media/Posts/" + postId;
            new File(dirPath).mkdirs();

            for (File file : selectedFiles) {
                String fileName = file.getName();
                try {
                    // Copy file to post directory
                    Files.copy(file.toPath(), new File(dirPath + "/" + fileName).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);

                    // Add to currentPhotos if not already present
                    if (!currentPhotos.contains(fileName)) {
                        currentPhotos.add(fileName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error adding photo: " + fileName,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            // Update database with new photo list
            updatePhotosInDatabase();
            // Refresh display
            loadPicture();
        }
    }

    private void deletePhotos() {
        if (currentPhotos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No photos to delete",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] options = currentPhotos.toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select photo to delete:",
                "Delete Photo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selected != null) {
            // Remove from list
            currentPhotos.remove(selected);

            // Delete file
            try {
                Files.deleteIfExists(new File("Project-code-v0.1/src/Media/Posts/" + postId + "/" + selected).toPath());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error deleting photo file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // Update database
            updatePhotosInDatabase();
            // Refresh display
            loadPicture();
        }
    }

    private void updatePhotosInDatabase() {
        String photosJson = String.join(",", currentPhotos);
        DBManager dbManager = new DBManager();
        if (!dbManager.updatePostPictures(postId, photosJson)) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update photos in database",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private File findImageInDirectory(File directory, String imageName) {
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Directory does not exist or is not a directory: " + directory.getPath());
            return null;
        }

        System.out.println("Searching for image: " + imageName);
        System.out.println("Starting search in directory: " + directory.getAbsolutePath());

        // First try direct match in the folder
        File directFile = new File(directory, imageName);
        if (directFile.exists() && directFile.isFile()) {
            System.out.println("Found image directly: " + directFile.getAbsolutePath());
            return directFile;
        }

        // If not found directly, search in subdirectories
        try {
            try (Stream<Path> paths = Files.walk(directory.toPath())) {
                return paths
                        .map(Path::toFile)
                        .filter(file -> {
                            if (file.isFile()) {
                                System.out.println("Checking file: " + file.getName());
                            }
                            return file.isFile() && file.getName().equals(imageName);
                        })
                        .findFirst()
                        .orElse(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Image getScaledImage(Image srcImg, int targetWidth, int targetHeight) {
        BufferedImage resizedImg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImg.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate dimensions that maintain aspect ratio
        double srcWidth = srcImg.getWidth(null);
        double srcHeight = srcImg.getHeight(null);

        double scaleFactor = Math.min(
                targetWidth / srcWidth,
                targetHeight / srcHeight
        );

        int scaledWidth = (int)(srcWidth * scaleFactor);
        int scaledHeight = (int)(srcHeight * scaleFactor);

        // Center the image
        int x = (targetWidth - scaledWidth) / 2;
        int y = (targetHeight - scaledHeight) / 2;

        // Draw black background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, targetWidth, targetHeight);

        // Draw the scaled image
        g2d.drawImage(srcImg, x, y, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return resizedImg;
    }

    private void loadCaption() {
        DBManager dbManager = new DBManager();
        String currentCaption = dbManager.getPostCaption(postId);

        if (currentCaption != null) {
            captionEdit.setText(currentCaption);
        } else {
            captionEdit.setText("");
            JOptionPane.showMessageDialog(this,
                    "Could not load post caption",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCaption() {
        String newCaption = captionEdit.getText().trim();

        if (newCaption.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Caption cannot be empty",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DBManager dbManager = new DBManager();
        if (dbManager.updatePostCaption(postId, newCaption)) {
            JOptionPane.showMessageDialog(this,
                    "Caption updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update caption",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

