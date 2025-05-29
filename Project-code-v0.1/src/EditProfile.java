import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.util.List;
import java.awt.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;




public class EditProfile extends JFrame {
    private JPanel Wrapper;
    private JButton backButton;
    private JButton saveButton;
    private JPanel controlPanel;
    private JButton addProfilePhotoButton;
    private JButton requestVerificationButton;
    private JPanel verificationPanel;
    private JPanel avatarPanel;
    private JButton selectPreferencesButton;
    private JPanel preferencesPanel;
    private JPanel profileInfo;
    private JTextField info;
    private JButton editPostsButton;
    private JPanel editPosts;


    public EditProfile(Integer user) {
        setTitle("Edit Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(Wrapper);
        setSize(375, 740);
        setResizable(false);
        setLocationRelativeTo(null);


        loadUserInfo(user);

        editPostsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserPosts(currentUser);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
                new Profile(currentUser);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Profile(user);
            }
        });
        addProfilePhotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectAndSaveAvatar();
            }
        });
        selectPreferencesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreferencesDialog(user);
            }
        });


        requestVerificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DBManager dbManager = new DBManager();

                if (dbManager.checkVerificationRequest(user)) {
                    JOptionPane.showMessageDialog(EditProfile.this,
                            "Verification has already been requested",
                            "Notice",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    if (dbManager.submitVerificationRequest(user)) {
                        JOptionPane.showMessageDialog(EditProfile.this,
                                "Verification request submitted successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        setVisible(true);
    }

    private Integer currentUser;


    private void showPreferencesDialog(Integer userId) {

        DefaultListModel<String> listModel = new DefaultListModel<>();
        String[] genres = {
                "Rock", "Pop", "Hip Hop", "Jazz", "Classical",
                "Electronic", "R&B", "Country", "Metal", "Folk",
                "Blues", "Reggae", "Soul", "Funk", "Punk",
                "Alternative", "Indie", "Latin", "Dance", "Gospel"
        };
        for (String genre : genres) {
            listModel.addElement(genre);
        }


        JList<String> genreList = new JList<>(listModel);
        genreList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        genreList.setVisibleRowCount(10);


        genreList.setBackground(Color.WHITE);
        genreList.setBorder(BorderFactory.createLineBorder(Color.GRAY));


        loadExistingPreferences(genreList, userId);


        JScrollPane scrollPane = new JScrollPane(genreList);
        scrollPane.setPreferredSize(new Dimension(200, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Select your music preferences:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);


        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Music Preferences",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );


        if (result == JOptionPane.OK_OPTION) {
            savePreferences(genreList.getSelectedValuesList(), userId);
        }
    }
    private void showUserPosts(Integer userId) {
        DBManager dbManager = new DBManager();
        ArrayList<Integer> userPosts = dbManager.getUserPosts(userId);

        if (userPosts == null || userPosts.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No posts found for this user",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Integer postId : userPosts) {
            listModel.addElement("Post ID: " + postId);
        }


        JList<String> postList = new JList<>(listModel);
        postList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        JScrollPane scrollPane = new JScrollPane(postList);
        scrollPane.setPreferredSize(new Dimension(200, 300));


        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Select a post to edit:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton editButton = new JButton("Edit Selected Post");
        JButton deleteButton = new JButton("Delete Post");
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);


        JDialog dialog = new JDialog(this, "Your Posts", true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);




        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = postList.getSelectedValue();
                if (selected != null) {

                    int postId = Integer.parseInt(selected.replace("Post ID: ", ""));


                    String postType = dbManager.getPostType(postId);
                    if (postType != null) {
                        dialog.dispose();

                        switch (postType.toLowerCase()) {
                            case "venue":
                                new EditVenueListing(postId);
                                break;
                            case "lesson":
                                new EditLessonListing(postId);
                                break;
                            case "event":
                            case "other":
                                new EditSimplePost(postId);
                                break;
                            default:
                                JOptionPane.showMessageDialog(dialog,
                                        "Unknown post type: " + postType,
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Could not determine post type",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Please select a post to edit",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = postList.getSelectedValue();
                if (selected != null) {
                    int postId = Integer.parseInt(selected.replace("Post ID: ", ""));


                    int confirm = JOptionPane.showConfirmDialog(dialog,
                            "Are you sure you want to delete this post?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (dbManager.deletePost(postId)) {
                            listModel.removeElement(selected);
                            JOptionPane.showMessageDialog(dialog,
                                    "Post deleted successfully",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);


                            if (listModel.isEmpty()) {
                                dialog.dispose();
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                    "Failed to delete post",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Please select a post to delete",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        dialog.setVisible(true);
    }


    private void loadExistingPreferences(JList<String> genreList, Integer userId) {
        DBManager dbManager = new DBManager();
        String preferences_json = dbManager.getUserPreferences(userId);

        if (preferences_json != null && !preferences_json.isEmpty()) {
            String[] selectedGenres = preferences_json.split(",");
            String[] allGenres = getListData(genreList);
            ArrayList<Integer> selectedIndices = new ArrayList<>();

            for (String selectedGenre : selectedGenres) {
                for (int j = 0; j < allGenres.length; j++) {
                    if (allGenres[j].equals(selectedGenre.trim())) {
                        selectedIndices.add(j);
                    }
                }
            }

            int[] indices = new int[selectedIndices.size()];
            for (int i = 0; i < selectedIndices.size(); i++) {
                indices[i] = selectedIndices.get(i);
            }
            genreList.setSelectedIndices(indices);
        }
    }

    private String[] getListData(JList<String> list) {
        ListModel<String> model = list.getModel();
        String[] data = new String[model.getSize()];
        for (int i = 0; i < model.getSize(); i++) {
            data[i] = model.getElementAt(i);
        }
        return data;
    }

    private void savePreferences(List<String> selectedGenres, Integer userId) {
        String preferences = String.join(",", selectedGenres);
        DBManager dbManager = new DBManager();

        if (dbManager.saveUserPreferences(userId, preferences)) {
            JOptionPane.showMessageDialog(this,
                    "Preferences saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error saving preferences",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUserInfo(Integer user) {
        this.currentUser = user;
        DBManager dbManager = new DBManager();
        ArrayList<Object> profileDetails = dbManager.getProfileDetails(user);

        if (profileDetails != null && !profileDetails.isEmpty()) {
            String username = profileDetails.get(0) != null ? profileDetails.get(0).toString() : "Unknown User";
            String bio = profileDetails.get(1) != null ? profileDetails.get(1).toString() : "No bio";
            boolean isVerified = profileDetails.size() >= 3 && profileDetails.get(2) != null && (Boolean) profileDetails.get(2);

            StringBuilder infoText = new StringBuilder();
            infoText.append("Username: ").append(username).append("\n");
            infoText.append("Bio: ").append(bio).append("\n");
            infoText.append("Status: ").append(isVerified ? "Verified" : "Unverified");

            info.setText(infoText.toString());
        } else {
            info.setText("Could not load user information");
        }
    }


    private void selectAndSaveAvatar() {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();


            Path destinationDir = Paths.get("Project-code-v0.1", "src", "Media", "UserAvatars", "1");

            System.out.println("Attempting to access: " + destinationDir.toAbsolutePath());
            if (!Files.exists(destinationDir)) {
                JOptionPane.showMessageDialog(this,
                        "Directory not found: " + destinationDir.toAbsolutePath(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Path destination = destinationDir.resolve(selectedFile.getName());
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                DBManager dbManager = new DBManager();
                if (dbManager.updateAvatarUrl(currentUser, selectedFile.getName())) {
                    JOptionPane.showMessageDialog(this, "Avatar updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error updating avatar in database",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Detailed error: " + e.getClass().getName() + ": " + e.getMessage() +
                                "\nTrying to save to: " + destinationDir.toAbsolutePath(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
