
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

public class EditLessonListing extends JFrame {
    private JPanel Wrapper;
    private JPanel postPic;
    private JPanel caption;
    private JTextField captionEdit;
    private JPanel controlPanel;
    private JButton backButton;
    private JButton confirmButton;
    private int postId;
    private JLabel imageLabel;

    public EditLessonListing(int postId) {
        this.postId = postId;


        setTitle("Edit Lesson Listing");
        setContentPane(Wrapper);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(375, 740);
        setLocationRelativeTo(null);
        setResizable(false);


        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        postPic.setLayout(new BorderLayout());
        postPic.add(imageLabel, BorderLayout.CENTER);


        loadCaption();
        loadPicture();


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCaption();
            }
        });

        setVisible(true);
    }

    private void loadPicture() {
        DBManager dbManager = new DBManager();
        String picturesJson = dbManager.getPostPictures(postId);

        if (picturesJson != null && !picturesJson.isEmpty()) {

            String firstPicture = picturesJson.split(",")[0].trim();

            try {

                String postsPath = "Project-code-v0.1/src/Media/Posts/" + postId;
                File postFolder = new File(postsPath);

                if (!postFolder.exists()) {
                    imageLabel.setText("Post folder not found: " + postsPath);
                    System.out.println("Post folder does not exist: " + postsPath);
                    return;
                }

                File imageFile = findImageInDirectory(postFolder, firstPicture);

                if (imageFile != null && imageFile.exists()) {
                    BufferedImage originalImage = ImageIO.read(imageFile);


                    int panelWidth = postPic.getWidth();
                    int panelHeight = postPic.getHeight();


                    if (panelWidth == 0) panelWidth = 360;
                    if (panelHeight == 0) panelHeight = 360;


                    Image scaledImage = getScaledImage(originalImage, panelWidth, panelHeight);


                    imageLabel.setIcon(new ImageIcon(scaledImage));
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

    private File findImageInDirectory(File directory, String imageName) {
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Directory does not exist or is not a directory: " + directory.getPath());
            return null;
        }

        System.out.println("Searching for image: " + imageName);
        System.out.println("Starting search in directory: " + directory.getAbsolutePath());


        File directFile = new File(directory, imageName);
        if (directFile.exists() && directFile.isFile()) {
            System.out.println("Found image directly: " + directFile.getAbsolutePath());
            return directFile;
        }


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


        double srcWidth = srcImg.getWidth(null);
        double srcHeight = srcImg.getHeight(null);

        double scaleFactor = Math.min(
                targetWidth / srcWidth,
                targetHeight / srcHeight
        );

        int scaledWidth = (int)(srcWidth * scaleFactor);
        int scaledHeight = (int)(srcHeight * scaleFactor);


        int x = (targetWidth - scaledWidth) / 2;
        int y = (targetHeight - scaledHeight) / 2;


        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, targetWidth, targetHeight);


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