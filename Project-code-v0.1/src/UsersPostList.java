import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UsersPostList extends JFrame{
    private JPanel Wrapper;
    private JButton backButton;
    private JButton viewCritiqueButton;
    private JButton sortByButton;
    private JLabel postsLabel;
    private JScrollPane postPanel;
    private JList<Post> postsList;
    private DefaultListModel<Post> postsListModel;
    private ArrayList<Post> currentPosts = new ArrayList<>();

    public UsersPostList() {
        setContentPane(Wrapper);
        setTitle("User's Posts");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);

        postsListModel = new DefaultListModel<>();
        postsList = new JList<>(postsListModel);
        postsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        postPanel.setViewportView(postsList);
        postsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Post) {
                    Post post = (Post) value;
                    setText("Post ID: " + post.getPost_id() + ", Desc: " + post.getDescription() + ", Type: " + post.getType() + ", Date: " + post.getDate());
                }
                return c;
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditCritiquesScreen(null); 
                dispose();
            }
        });
        viewCritiqueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Post selectedPost = postsList.getSelectedValue();
                if (selectedPost == null) {
                    JOptionPane.showMessageDialog(UsersPostList.this, "Please select a post to view critiques.");
                    return;
                }
                DBManager dbManager = new DBManager();
                ArrayList<Review> reviews = dbManager.getReviewsForPost(selectedPost.getPost_id());
                if (reviews.isEmpty()) {
                    JOptionPane.showMessageDialog(UsersPostList.this, "No critiques found for this post.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Review review : reviews) {
                        sb.append("Review ID: ").append(review.getReviewId())
                          .append(", Rating: ").append(review.getRating())
                          .append(", Content: ").append(review.getContent())
                          .append("\n");
                    }
                    JOptionPane.showMessageDialog(UsersPostList.this, sb.toString(), "Critiques for Post ID " + selectedPost.getPost_id(), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        sortByButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPosts.isEmpty()) {
                    JOptionPane.showMessageDialog(UsersPostList.this, "No posts to sort.");
                    return;
                }
                new SortBy(new ArrayList<>(currentPosts), sortedPosts -> {
                    currentPosts.clear();
                    currentPosts.addAll(sortedPosts);
                    postsListModel.clear();
                    for (Post post : currentPosts) {
                        postsListModel.addElement(post);
                    }
                });
            }
        });
    }

    public void displayUserPosts(int userId) {
        DBManager dbManager = new DBManager();
        ArrayList<Post> posts = dbManager.getPosts(userId);
        postsListModel.clear();
        currentPosts.clear();
        if (posts.isEmpty()) {
            postsListModel.addElement(new Post(-1, -1, "No posts found.", "", "", "", "", 0));
        } else {
            for (Post post : posts) {
                postsListModel.addElement(post);
                currentPosts.add(post);
            }
        }
    }
}
