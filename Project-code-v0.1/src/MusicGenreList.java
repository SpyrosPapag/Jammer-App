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


public class MusicGenreList extends JFrame {
    public JPanel Wrapper;
    private JList<String> MusicGenres;
    private JButton savePicks;
    private JLabel screenLabel;
    private JButton backButton;

    public static int currentUserId;

    public MusicGenreList(Integer userId) {
        setTitle("Music Genre List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(Wrapper);
        pack();
        setLocationRelativeTo(null);

        this.currentUserId = userId;

        // Back button action
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProfileInfoCompletion(userId);
            }
        });

        // Save button action
        savePicks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePreferences(MusicGenres.getSelectedValuesList(), userId);
                new ProfileInfoCompletion(userId);
            }
        });
    }



    private void showPreferencesDialog(Integer userId) {
        // Create a DefaultListModel and populate it with genres
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

        // Create JList with the model
        JList<String> genreList = new JList<>(listModel);
        genreList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        genreList.setVisibleRowCount(10); // Show 10 items at a time

        // Set some visual properties
        genreList.setBackground(Color.WHITE);
        genreList.setBorder(BorderFactory.createLineBorder(Color.GRAY));


        // Create scroll pane with proper size
        JScrollPane scrollPane = new JScrollPane(genreList);
        scrollPane.setPreferredSize(new Dimension(200, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Create a panel to hold the components
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Select your music preferences:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Music Preferences",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // Handle result
        if (result == JOptionPane.OK_OPTION) {
            savePreferences(genreList.getSelectedValuesList(), userId);
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

}