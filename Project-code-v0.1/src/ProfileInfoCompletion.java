import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ProfileInfoCompletion {
    private JPanel Wrapper;
    private JTextField username;
    private JTextField AboutMe;
    private JButton AddPhoto;
    private JButton SetPreferences;
    private JButton saveProfileButton;

    public static int user;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public ProfileInfoCompletion(int user) {
        ProfileInfoCompletion.user = user;

        initUI();
    }

    public ProfileInfoCompletion() {
        initUI();
    }


    private void initUI() {

        Wrapper = new JPanel();
        username = new JTextField(20);
        AboutMe = new JTextField(30);
        AddPhoto = new JButton("Add Photo");
        SetPreferences = new JButton("Set Preferences");
        saveProfileButton = new JButton("Save Profile");


        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);


        JPanel usernamePanel = new JPanel(new GridBagLayout());
        usernamePanel.add(username);


        JPanel otherPanel = new JPanel();
        otherPanel.setLayout(new BoxLayout(otherPanel, BoxLayout.Y_AXIS));
        otherPanel.add(AboutMe);
        otherPanel.add(AddPhoto);
        otherPanel.add(SetPreferences);
        otherPanel.add(saveProfileButton);


        cardPanel.add(usernamePanel, "USERNAME");
        cardPanel.add(otherPanel, "OTHER");


        Wrapper.setLayout(new BorderLayout());
        Wrapper.removeAll();
        Wrapper.add(cardPanel, BorderLayout.CENTER);


        cardLayout.show(cardPanel, "USERNAME");


        username.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "OTHER");
            }
        });
        AddPhoto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new photoMenu(String.valueOf(user));
            }
        });
        SetPreferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MusicGenreList(user);
            }
        });
        saveProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usernameText = username.getText().trim();
                String bio = AboutMe.getText().trim();
                if (updateUserBio(usernameText, bio)) {
                    JOptionPane.showMessageDialog(null, "Profile updated!");
                    SwingUtilities.getWindowAncestor(Wrapper).dispose();
                    new Login();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update profile.");
                }
            }
        });
    }

    public boolean updateUserBio(String username, String bio) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET bio = ? WHERE username = ?")) {
                ps.setString(1, bio);
                ps.setString(2, username);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public JPanel getWrapper() {
        return Wrapper;
    }

}