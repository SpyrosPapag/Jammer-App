import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Profile extends JFrame{
    private JPanel newEdit_Panel;
    private JButton newPostButton;
    private JButton editProfileButton;
    private JPanel pagesPanel;
    private JButton listingsButton;
    private JButton chatButton;
    private JButton profileButton;
    private JButton notifsButton;
    private JButton eventsButton;
    private JScrollPane postsPanel;
    private JPanel Wrapper;
    private JPanel profileInfoPanel;

    public Profile(Integer user)
    {
        setContentPane(Wrapper);
        setTitle("Profile");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);

        viewProfile(postsPanel, profileInfoPanel, user);

//        newPostButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                new newPost();
//            }
//        });
//
//        editProfileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                new editProfile();
//            }
//        });

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
//        chatButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                new ChatScreen();
//            }
//        });

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



    private void viewProfile(JScrollPane postsPanel, JPanel profileInfoPanel, Integer user)
    {
        // fetch and display user`s info
        DBManager prflManager = new DBManager();
        String info = prflManager.getProfileInfo(user);

        displayUserInfo(profileInfoPanel, info);

        // fetch and display user`s posts
        ArrayList<Post> postsToDisplay = prflManager.getPosts(user);

        Post.displayPosts(postsToDisplay, postsPanel);
    }

    private void displayUserInfo(JPanel profileInfoPanel, String info)
    {
        return;
    }
}
