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
    private ArrayList<Post> postsToDisplay;

    public Events(Integer user)
    {
        setContentPane(Wrapper);
        setTitle("Events");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);

        // fetch and display users suggested events feed
        viewFeed(feedPanel, user, "events");

        /*filterButton.addActionListener(e -> {
            new Filter(postsToDisplay, filtered -> {
                if(!filtered.isEmpty())
                    JOptionPane.showMessageDialog(null, "No results for filter.", "No results", JOptionPane.INFORMATION_MESSAGE);
                else
                    refreshFeed(filtered);
            });
        });*/

        sortByButton.addActionListener(e -> {
            if(postsToDisplay.isEmpty())
                JOptionPane.showMessageDialog(null, "Nothing to sort", "Nothing to sort", JOptionPane.INFORMATION_MESSAGE);
            else
                new SortBy(postsToDisplay, sorted -> {
                    refreshFeed(sorted);
                });
        });

//
//        listingsButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                new Listings();
//            }
//        });
//
//        notifsButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                new NotificationsScreen();
//            }
//        });
//
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Chats(user);
            }
        });

        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Profile(user);
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

    private void viewFeed(JScrollPane feedPanel, Integer user, String type)
    {
        // get user`s suggested posts
        Suggested suggester = new Suggested();
        postsToDisplay = suggester.suggestPosts(user, type);
        if(postsToDisplay.isEmpty()) return;

        // display the returned posts
        Post.displayPosts(postsToDisplay, feedPanel);
    }

    public void refreshFeed(ArrayList<Post> newPosts) {
        this.postsToDisplay = newPosts;
        Post.displayPosts(postsToDisplay, feedPanel);
    }

}