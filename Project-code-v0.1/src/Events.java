import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Events extends JFrame{
    private JPanel Wrapper;
    private JPanel filterSort_Panel;
    private JPanel pagesPanel;
    private JButton filterButton;
    private JButton sortByButton;
    private JButton listingsButton;
    private JButton chatButton;
    private JButton profileButton;
    private JButton notifsButton;
    private JButton eventsButton;
    private JScrollPane feedPanel;


    public Events(Integer user)
    {
        setContentPane(Wrapper);
        setTitle("Events");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360,740);
        setResizable(false);
        setLocationRelativeTo(null);

        // Load user`s suggested posts
        Suggested suggester = new Suggested();
        ArrayList<Post> postsToDisplay = suggester.suggestPosts(user, "events");

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Filter();
            }
        });

        sortByButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SortBy();
            }
        });

        listingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Listings();
            }
        });

        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Profile();
            }
        });

        notifsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new NotificationsScreen();
            }
        });

        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ChatScreen();
            }
        });

        eventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Events(user);
            }
        });
    }



}
