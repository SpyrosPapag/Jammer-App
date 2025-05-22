import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import java.util.*;
import java.util.List;

public class Post {

    private Integer post_id, poster_id;
    private String description;

    public List<String> getPictures_url_json() {
        return Arrays.asList(pictures_url_json.split(","));
    }

    public String getDescription() {
        return description;
    }

    public Integer getPoster_id() {
        return poster_id;
    }

    public Integer getPost_id() {
        return post_id;
    }

    private String pictures_url_json;

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public void setPoster_id(Integer poster_id) {
        this.poster_id = poster_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPictures_url_json(String pictures_url_json) {
        this.pictures_url_json = pictures_url_json;
    }

    public Post(Integer post_id, Integer poster_id, String description, String pictures_url_json)
    {
        setPost_id(post_id);
        setPoster_id(poster_id);
        setDescription(description);
        setPictures_url_json(pictures_url_json);
    }

    public static void displayPosts(ArrayList<Post> postsToDisplay, JScrollPane displayPanel)
    {
        // panel for all posts
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        for (Post post : postsToDisplay)
        {
            // panel for one post
            JPanel postPanel = new JPanel(new BorderLayout(0, 40));
            postPanel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
            postPanel.setBackground(Color.lightGray);
            Dimension dims = new Dimension(357, 250);
            postPanel.setPreferredSize(dims);
            postPanel.setMinimumSize(dims);
            postPanel.setMaximumSize(dims);

            // text section
            JLabel textLabel = new JLabel(post.getDescription());
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            postPanel.add(textLabel, BorderLayout.SOUTH);

            // photos section
            List<String> urls = post.getPictures_url_json();
            JPanel cardPanel = new JPanel(new CardLayout());
            cardPanel.setPreferredSize(new Dimension(360, 250));
            cardPanel.setBackground(Color.lightGray);
            Integer id = post.getPost_id();
            for (String url : urls)
            {
                String path = "/Media/Posts/" + id.toString() + "/" + url;
                URL imgUrl = Events.class.getResource(path);
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(280, 200, Image.SCALE_SMOOTH);
                JLabel picLabel = new JLabel(new ImageIcon(img));
                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setBackground(Color.lightGray);
                wrapper.add(picLabel, BorderLayout.CENTER);
                cardPanel.add(wrapper, url);
            }
            // navigation buttons
            JButton prevBtn = new JButton("p");
            JButton nextBtn = new JButton("n");
            prevBtn.setPreferredSize(new Dimension(42, 250));
            nextBtn.setPreferredSize(new Dimension(42, 250));

            // listeners
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            prevBtn.addActionListener(e -> cl.previous(cardPanel));
            nextBtn.addActionListener(e -> cl.next(cardPanel));

            // assemble
            JPanel carouselContainer = new JPanel(new BorderLayout());
            carouselContainer.setBackground(Color.LIGHT_GRAY);
            if(urls.size() > 1)
            {
                carouselContainer.add(prevBtn, BorderLayout.WEST);
                carouselContainer.add(nextBtn, BorderLayout.EAST);
            }
            carouselContainer.add(cardPanel, BorderLayout.CENTER);
            postPanel.add(carouselContainer, BorderLayout.CENTER);

            container.add(postPanel);
            container.add(Box.createVerticalStrut(5));
        }

        // add container to scroll pane
        displayPanel.setViewportView(container);

        // refresh
        container.revalidate();
        container.repaint();
    }
}
