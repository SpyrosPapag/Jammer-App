import javax.swing.*;
import java.util.ArrayList;

public class PostList {
    private JPanel Wrapper;
    private JButton sortByButton;
    private JButton backButton;
    private JButton deleteCritiqueButton;
    private JButton addCritiqueButton;
    private JLabel postsLabel;

    public PostList() {
        backButton.addActionListener(e -> {
            new EditCritiquesScreen(null);
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(backButton);
            if (topFrame != null) topFrame.dispose();
        });
    }

    public void displayOtherUsersPosts(int currentUserId) {
        DBManager dbManager = new DBManager();
        ArrayList<Post> allPosts = dbManager.getAllPosts();
        StringBuilder sb = new StringBuilder();
        for (Post post : allPosts) {
            if (post.getPoster_id() != currentUserId) {
                sb.append("Post ID: ").append(post.getPost_id())
                  .append(", Desc: ").append(post.getDescription())
                  .append(", Type: ").append(post.getType())
                  .append(", Date: ").append(post.getDate())
                  .append("<br>");
            }
        }
        if (sb.isEmpty()) {
            postsLabel.setText("No posts from other users found.");
        } else {
            postsLabel.setText("<html>" + sb + "</html>");
        }
    }
}
