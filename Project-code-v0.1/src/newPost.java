import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class newPost extends JFrame{
    private JPanel Wrapper;
    private JButton choosePhotosButton;
    private JButton backButton;
    private JButton postButton;
    private JPanel descriptionPanel;
    public JTextField descTF;
    private JPanel picPanel;
    private JLabel photoLbl;
    private JPanel radioPanel;
    public JRadioButton eventRadio;
    public JRadioButton venueRadio;
    public JRadioButton lessonRadio;
    public JRadioButton otherRadio;
    private JPanel locationPanel;
    private JPanel datePanel;
    public JLabel dateLbl;
    public JLabel locationLbl;
    private JButton dateButton;
    private File[] chosenPhotos = null;
    private String tag = null;
    private String description = null;
    public Integer tmp_user = 0;
    private String date;
    private String location;

    public newPost(Integer user)
    {
        tmp_user = user;
        setContentPane(Wrapper);
        setTitle("NewPost");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);

        // radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(eventRadio);
        group.add(venueRadio);
        group.add(lessonRadio);
        group.add(otherRadio);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Profile(user);
            }
        });

        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // tag
                if     (eventRadio.isSelected())  tag = "event";
                else if(venueRadio.isSelected())  tag = "venue";
                else if(lessonRadio.isSelected()) tag = "lesson";
                else if(otherRadio.isSelected())  tag = "other";

                // description
                description = descTF.getText();

                // date
                date = dateLbl.getText();

                // location
                location = locationLbl.getText();

                // validate inputs
                if(validateUserInputs(tag, description, chosenPhotos))
                {
                    // photos
                    StringBuilder photo_urls = new StringBuilder();
                    int count = 0;
                    for(File photo : chosenPhotos)
                    {
                        count++;
                        photo_urls.append(photo.getName());
                        if(count != chosenPhotos.length)
                            photo_urls.append(",");
                    }

                    // new Post object
                    Post post = new Post(null, user, description, photo_urls.toString(), tag, date, location, null);

                    // insert new Post to DB
                    DBManager newPostManager = new DBManager();
                    Integer post_id = newPostManager.addPost(post);

                    if(post_id != 0)
                    {
                        // copy photos to paths to display later
                        for(File file : chosenPhotos)
                        {
                            try
                            {
                                Path newDir1 = Paths.get("out/production/Jammer-App/Media/Posts/" + post_id.toString());
                                Path newDir2 = Paths.get("out/production/Jammer-App/Posts/" + post_id.toString());
                                Path newDir3 = Paths.get("Project-code-v0.1/src/Media/Posts/" + post_id.toString());

                                Files.createDirectories(newDir1);
                                Files.createDirectories(newDir2);
                                Files.createDirectories(newDir3);

                                Path dest1 = newDir1.resolve(file.getName());
                                Path dest2 = newDir2.resolve(file.getName());
                                Path dest3 = newDir3.resolve(file.getName());

                                Files.copy(file.toPath(), dest1, StandardCopyOption.REPLACE_EXISTING);
                                Files.copy(file.toPath(), dest2, StandardCopyOption.REPLACE_EXISTING);
                                Files.copy(file.toPath(), dest3, StandardCopyOption.REPLACE_EXISTING);
                            }
                            catch (IOException ex)
                            {
                                ex.printStackTrace();
                            }
                        }

                        post.setPost_id(post_id);
                        Notification.notify(post);
                    }
                        dispose();
                        new Profile(user);
                }
                else
                {
                    // invalid inputs screen
                    JOptionPane.showMessageDialog(null, "Invalid inputs", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        choosePhotosButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "PNG"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                chosenPhotos = chooser.getSelectedFiles();
                BufferedImage original = null;

                List<BufferedImage> pics = new ArrayList<>();
                List<String> names = new ArrayList<>();
                for(File photo : chosenPhotos)
                {
                    try
                    {
                        original = ImageIO.read(photo);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }

                    if (original == null)
                    {
                        ImageIcon tmpIcon = new ImageIcon(photo.getAbsolutePath());
                        Image tmp = tmpIcon.getImage();
                        int ow = tmp.getWidth(null), oh = tmp.getHeight(null);
                        if (ow > 0 && oh > 0)
                        {
                            original = new BufferedImage(ow, oh, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g = original.createGraphics();
                            g.drawImage(tmp, 0, 0, null);
                            g.dispose();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(this,
                                    "Could not load image data from: " + chosenPhotos,
                                    "Load Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    int w = 280, h = 280;
                    BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = scaled.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(original, 0, 0, w, h, null);
                    g2d.dispose();

                    pics.add(scaled);
                    names.add(photo.getName());
                }
                picPanel.add(Post.displayMultiplePhotos(pics, names, this), BorderLayout.CENTER);
                picPanel.revalidate();
                picPanel.repaint();
            }
        });

        datePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String input = JOptionPane.showInputDialog(null,"Enter a date (e.g. 2025-05-24):","Date Input",JOptionPane.QUESTION_MESSAGE);

                if (input != null && !input.isEmpty())
                {
                    try
                    {
                        LocalDate date = LocalDate.parse(input);  // assumes ISO format yyyy-MM-dd
                        dateLbl.setText(date.toString());
                    }
                    catch (DateTimeParseException ex)
                    {
                        JOptionPane.showMessageDialog(null, "Invalid date format!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        locationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String input = JOptionPane.showInputDialog(null,"Enter a location:","Location Input",JOptionPane.QUESTION_MESSAGE);

                if (input != null && !input.isEmpty())
                    locationLbl.setText(input);
            }
        });
    }

    public newPost(Integer user, String tag_in, String description_in, List<BufferedImage> pics_in, List<String> names_in, String date_in, String location_in)
    {
        tmp_user = user;
        setContentPane(Wrapper);
        setTitle("NewPost");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);

        // set previous user choices
        if      (Objects.equals(tag_in, "event")) eventRadio.setSelected(true);
        else if (Objects.equals(tag_in, "venue")) venueRadio.setSelected(true);
        else if (Objects.equals(tag_in, "lesson")) lessonRadio.setSelected(true);
        else if (Objects.equals(tag_in, "other")) otherRadio.setSelected(true);

        descTF.setText(description_in);
        dateLbl.setText(date_in);
        locationLbl.setText(location_in);

        File[] tmpImages = new File[pics_in.size()];
        for (int i = 0; i < pics_in.size(); i++)
        {
            BufferedImage img = pics_in.get(i);
            String name = names_in.get(i);

            File outFile = new File("photos_temp_" + name);

            try
            {
                ImageIO.write(img, "png", outFile);
                tmpImages[i] = outFile;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        chosenPhotos = tmpImages;

        picPanel.add(Post.displayMultiplePhotos(pics_in, names_in, this), BorderLayout.CENTER);
        picPanel.revalidate();
        picPanel.repaint();

        // radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(eventRadio);
        group.add(venueRadio);
        group.add(lessonRadio);
        group.add(otherRadio);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (File file : tmpImages) file.delete();
                dispose();
                new Profile(user);
            }
        });


        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // tag
                if     (eventRadio.isSelected())  tag = "event";
                else if(venueRadio.isSelected())  tag = "venue";
                else if(lessonRadio.isSelected()) tag = "lesson";
                else if(otherRadio.isSelected())  tag = "other";

                // description
                description = descTF.getText();

                // date
                date = dateLbl.getText();

                // location
                location = locationLbl.getText();

                // validate inputs
                if(validateUserInputs(tag, description, chosenPhotos))
                {
                    // photos
                    StringBuilder photo_urls = new StringBuilder();
                    int count = 0;
                    for(File photo : chosenPhotos)
                    {
                        count++;
                        photo_urls.append(photo.getName());
                        if(count != chosenPhotos.length)
                            photo_urls.append(",");
                    }

                    // new Post object
                    Post post = new Post(null, user, description, photo_urls.toString(), tag, date, location, null);

                    // insert new Post to DB
                    DBManager newPostManager = new DBManager();
                    Integer post_id = newPostManager.addPost(post);

                    if(post_id != 0)
                    {
                        // copy photos to paths to display later
                        for(File file : chosenPhotos)
                        {
                            try
                            {
                                Path newDir1 = Paths.get("out/production/Jammer-App/Media/Posts/" + post_id.toString());
                                Path newDir2 = Paths.get("out/production/Jammer-App/Posts/" + post_id.toString());
                                Path newDir3 = Paths.get("Project-code-v0.1/src/Media/Posts/" + post_id.toString());

                                Files.createDirectories(newDir1);
                                Files.createDirectories(newDir2);
                                Files.createDirectories(newDir3);

                                Path dest1 = newDir1.resolve(file.getName());
                                Path dest2 = newDir2.resolve(file.getName());
                                Path dest3 = newDir3.resolve(file.getName());

                                Files.copy(file.toPath(), dest1, StandardCopyOption.REPLACE_EXISTING);
                                Files.copy(file.toPath(), dest2, StandardCopyOption.REPLACE_EXISTING);
                                Files.copy(file.toPath(), dest3, StandardCopyOption.REPLACE_EXISTING);
                            }
                            catch (IOException ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }
                    for (File file : tmpImages) file.delete();
                    dispose();
                    new Profile(user);
                }
                else
                {
                    // invalid inputs screen
                    JOptionPane.showMessageDialog(null, "Invalid inputs", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        choosePhotosButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "PNG"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                chosenPhotos = chooser.getSelectedFiles();
                BufferedImage original = null;

                List<BufferedImage> pics = new ArrayList<>();
                List<String> names = new ArrayList<>();
                for(File photo : chosenPhotos)
                {
                    try
                    {
                        original = ImageIO.read(photo);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }

                    if (original == null)
                    {
                        ImageIcon tmpIcon = new ImageIcon(photo.getAbsolutePath());
                        Image tmp = tmpIcon.getImage();
                        int ow = tmp.getWidth(null), oh = tmp.getHeight(null);
                        if (ow > 0 && oh > 0)
                        {
                            original = new BufferedImage(ow, oh, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g = original.createGraphics();
                            g.drawImage(tmp, 0, 0, null);
                            g.dispose();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(this,
                                    "Could not load image data from: " + chosenPhotos,
                                    "Load Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    int w = 280, h = 280;
                    BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = scaled.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(original, 0, 0, w, h, null);
                    g2d.dispose();

                    pics.add(scaled);
                    names.add(photo.getName());
                }
                picPanel.add(Post.displayMultiplePhotos(pics, names, this), BorderLayout.CENTER);
                picPanel.revalidate();
                picPanel.repaint();
            }
        });

        datePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String input = JOptionPane.showInputDialog(null,"Enter a date (e.g. 2025-05-24):","Date Input",JOptionPane.QUESTION_MESSAGE);

                if (input != null && !input.isEmpty())
                {
                    try
                    {
                        LocalDate date = LocalDate.parse(input);  // assumes ISO format yyyy-MM-dd
                        dateLbl.setText(date.toString());
                    }
                    catch (DateTimeParseException ex)
                    {
                        JOptionPane.showMessageDialog(null, "Invalid date format!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        locationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String input = JOptionPane.showInputDialog(null,"Enter a location:","Location Input",JOptionPane.QUESTION_MESSAGE);

                if (input != null && !input.isEmpty())
                    locationLbl.setText(input);
            }
        });
    }

    private boolean validateUserInputs(String tag, String description, File[] file)
    {
        if(tag == null || Objects.equals(description, "") || file == null)
            return false;
        else
            return true;
    }
}
