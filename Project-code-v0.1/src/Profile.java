import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

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
    private JLabel Bio;
    private JLabel Avatar;
    private JLabel Other;

    public Profile(Integer user)
    {
        setContentPane(Wrapper);
        setTitle("Profile");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);
        viewProfile(postsPanel, Bio, Avatar, Other, user);

        newPostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new newPost(user);
            }
        });
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

    public Profile(Integer logedInUser, Integer otherUser)
    {
        setContentPane(Wrapper);
        setTitle("Profile");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);
        viewProfile(postsPanel, Bio, Avatar, Other, otherUser);

        newPostButton.setVisible(false);
        editProfileButton.setVisible(false);
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
                new Profile(logedInUser);
            }
        });

        eventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Events(logedInUser);
            }
        });
    }

    private void viewProfile(JScrollPane postsPanel, JLabel Bio, JLabel Avatar, JLabel Other, Integer user)
    {
        // fetch and display user`s info
        DBManager prflManager = new DBManager();
        ArrayList<Object> info = prflManager.getProfileInfo(user);

        displayUserInfo(Bio, Avatar, Other, info);

        // fetch and display user`s posts
        ArrayList<Post> postsToDisplay = prflManager.getPosts(user);

        Post.displayPosts(postsToDisplay, postsPanel);
    }

    private void displayUserInfo(JLabel Bio, JLabel Avatar, JLabel Other, ArrayList<Object> info)
    {
        // set avatar
        String path = "/Media/UserAvatars/" + info.get(0).toString() + "/" + info.get(1).toString();
        URL imgUrl = Events.class.getResource(path);
        ImageIcon icon = new ImageIcon(imgUrl);
        Image img = icon.getImage().getScaledInstance(100,100,Image.SCALE_SMOOTH);
        Avatar.setIcon(new ImageIcon(img));

        // set bio
        Bio.setText(info.get(2).toString());

        // set other
        String text;
        if((Boolean) info.get(3))
            text = "Verified Gigachad";
        else
            text = "Unverified Bad player";
        Other.setText(text);

    }
}
