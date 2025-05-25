import javax.swing.*;
import java.awt.*;

public class PostScreen {
    private JPanel Wrapper;
    private JPanel pagesPanel;
    private JButton listingsButton;
    private JButton chatButton;
    private JButton profileButton;
    private JButton notifsButton;
    private JButton eventsButton;
    private JScrollPane infoPanel;
    private JButton interestedButton;

    public PostScreen(Post post) {
        // panel for all posts
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

        JPanel avatarPanel = new JPanel(new BorderLayout(0, 40));
        avatarPanel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        avatarPanel.setBackground(Color.lightGray);
        avatarPanel.setPreferredSize(dims);
        avatarPanel.setMinimumSize(dims);
        avatarPanel.setMaximumSize(new Dimension(357, 400));


        // add container to scroll pane
        infoPanel.setViewportView(container);

        // refresh
        container.revalidate();
        container.repaint();
    }
}
