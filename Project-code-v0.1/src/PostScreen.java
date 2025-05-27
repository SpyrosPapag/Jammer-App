import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;

public class PostScreen extends JFrame {
    private JPanel Wrapper;
    private JPanel pagesPanel;
    private JButton listingsButton;
    private JButton chatButton;
    private JButton profileButton;
    private JButton notifsButton;
    private JButton eventsButton;
    private JScrollPane infoPanel;
    private JButton interestedButton;
    private JLabel Avatar;

    public PostScreen(Post post) {

        setContentPane(Wrapper);
        setTitle(post.getType().substring(0, 1).toUpperCase() + post.getType().substring(1));
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);

        DBManager postScrnManager = new DBManager();
        if(postScrnManager.isInterested(Main.loggeduser)){
            interestedButton.setEnabled(true);
        }
        else
            interestedButton.setEnabled(false);

        interestedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                interestedButton.setEnabled(false);
                postScrnManager.expressInterest(Main.loggeduser, post);
            }
        });

        listingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Listings(Main.loggeduser);
            }
        });

        notifsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new NotificationScreen(Main.loggeduser);
            }
        });

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
                                                     new Profile(Main.loggeduser);
                                                 }
                                             });

        eventsButton.addActionListener(new ActionListener() {
                                                 @Override
                                                 public void actionPerformed(ActionEvent e) {
                                                     dispose();
                                                     new Events(Main.loggeduser);
                                                 }
                                             });

        // panel for post info
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));


        // panel for one post
        JPanel postPanel = new JPanel(new BorderLayout(0, 40));
        postPanel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        postPanel.setBackground(Color.lightGray);
        Dimension dims = new Dimension(357, 360);
        postPanel.setPreferredSize(dims);
        postPanel.setMinimumSize(dims);
        postPanel.setMaximumSize(new Dimension(357, 400));

        // text section
        JLabel textLabel = new JLabel("<html> description: " + post.getDescription() + "<br> date: " + post.getDate() + "<br> popularity: " + post.getPopularity() + "<br> location: " + post.getLocation() + "</html>");
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        postPanel.add(textLabel, BorderLayout.SOUTH);

        // photos section
        postPanel.add(Post.displayMultiplePhotos(post), BorderLayout.CENTER);

        container.add(postPanel);
        container.add(Box.createVerticalStrut(5));

        ArrayList<Object> info = postScrnManager.getProfileInfo(post.getPoster_id());
        String path = "/Media/UserAvatars/" + info.get(0).toString() + "/" + info.get(1).toString();
        URL imgUrl = Events.class.getResource(path);
        ImageIcon icon = new ImageIcon(imgUrl);
        Image img = icon.getImage().getScaledInstance(100,100,Image.SCALE_SMOOTH);
        Avatar.setIcon(new ImageIcon(img));
        Avatar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                dispose();
                new Profile(post.getPoster_id());
            }
        });

        // add container to scroll pane
        infoPanel.setViewportView(container);

        // refresh
        container.revalidate();
        container.repaint();
    }
}
