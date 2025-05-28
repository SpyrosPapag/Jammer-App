import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.*;
import java.util.List;

public class Post {
    private Integer post_id, poster_id, popularity;
    private String description, type, location, date;

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPictures_url_json() {
        return pictures_url_json;
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

    public Post(Integer post_id, Integer poster_id, String description, String pictures_url_json, String type, String date, String location, Integer popularity)
    {
        setPost_id(post_id);
        setPoster_id(poster_id);
        setDescription(description);
        setPictures_url_json(pictures_url_json);
        setType(type);
        setDate(date);
        setLocation(location);
        setPopularity(popularity);
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
            Dimension dims = new Dimension(357, 360);
            postPanel.setPreferredSize(dims);
            postPanel.setMinimumSize(dims);
            postPanel.setMaximumSize(new Dimension(357, 400));

            // text section
            JLabel textLabel = new JLabel("<html> description: " + post.getDescription() + "<br> date: " + post.getDate() + "<br> popularity: " + post.getPopularity() + "<br> location: " + post.getLocation() + "</html>");
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            postPanel.add(textLabel, BorderLayout.SOUTH);

            // photos section
            postPanel.add(displayMultiplePhotos(post), BorderLayout.CENTER);

            postPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e){
                    SwingUtilities.getWindowAncestor(displayPanel).dispose();
                    new PostScreen(post);
                }
            });
            container.add(postPanel);
            container.add(Box.createVerticalStrut(5));
        }

        // add container to scroll pane
        displayPanel.setViewportView(container);

        // refresh
        container.revalidate();
        container.repaint();
    }

    public static JPanel displayMultiplePhotos(Post post)
    {
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.setPreferredSize(new Dimension(360, 280));
        cardPanel.setBackground(Color.lightGray);
        Integer id = post.getPost_id();
        List<String> urls = Arrays.asList(post.getPictures_url_json().split(","));
        for (String url : urls)
        {
            String path = "/Media/Posts/" + id.toString() + "/" + url;
            URL imgUrl = Events.class.getResource(path);
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
            JLabel picLabel = new JLabel(new ImageIcon(img));
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setBackground(Color.lightGray);
            wrapper.add(picLabel, BorderLayout.CENTER);
            cardPanel.add(wrapper, url);
        }
        // navigation buttons
        JButton prevBtn = new JButton("<");
        JButton nextBtn = new JButton(">");
        prevBtn.setPreferredSize(new Dimension(42, 280));
        nextBtn.setPreferredSize(new Dimension(42, 280));

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
        return carouselContainer;
    }

    public static JPanel displayMultiplePhotos(List<BufferedImage> pics, List<String> names, newPost info)
    {
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.setPreferredSize(new Dimension(360, 280));
        cardPanel.setBackground(Color.lightGray);
        int count = 0;
        for (BufferedImage pic : pics)
        {
            JLabel picLabel = new JLabel(new ImageIcon(pic));
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.add(picLabel, BorderLayout.CENTER);

            JButton editBtn = new JButton("Edit");
            int finalCount = count;
            editBtn.addActionListener(e -> {
                info.dispose();
                new editPhoto(pics, names, finalCount, info);
            });
            wrapper.add(editBtn, BorderLayout.NORTH);

            cardPanel.add(wrapper, names.get(count));
            count++;
        }
        // navigation buttons
        JButton prevBtn = new JButton("<");
        JButton nextBtn = new JButton(">");
        prevBtn.setPreferredSize(new Dimension(42, 280));
        nextBtn.setPreferredSize(new Dimension(42, 280));

        // listeners
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        prevBtn.addActionListener(e -> cl.previous(cardPanel));
        nextBtn.addActionListener(e -> cl.next(cardPanel));

        // assemble
        JPanel carouselContainer = new JPanel(new BorderLayout());
        carouselContainer.setBackground(Color.LIGHT_GRAY);
        if(pics.size() > 1)
        {
            carouselContainer.add(prevBtn, BorderLayout.WEST);
            carouselContainer.add(nextBtn, BorderLayout.EAST);
        }
        carouselContainer.add(cardPanel, BorderLayout.CENTER);
        return carouselContainer;
    }
}
