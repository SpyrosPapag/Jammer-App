import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;

public class SortBy extends JFrame{
    private JButton dateButton;
    private JPanel Wrapper;
    private JButton distanceButton;
    private JButton popularityButton;
    private Consumer<ArrayList<Post>> callback;

    public SortBy(ArrayList<Post> postsToDisplay, Consumer<ArrayList<Post>> callback)
    {
        this.callback = callback;
        setContentPane(Wrapper);
        setTitle("SortBy");
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360,200);
        setResizable(false);
        setLocationRelativeTo(null);


        dateButton.addActionListener(e -> {
            sort(postsToDisplay, "date");
            callback.accept(postsToDisplay);
            dispose();
        });


        distanceButton.addActionListener(e -> {
            sort(postsToDisplay, "distance");
            callback.accept(postsToDisplay);
            dispose();
        });

        popularityButton.addActionListener(e -> {
            sort(postsToDisplay, "popularity");
            callback.accept(postsToDisplay);
            dispose();
        });
    }

    public static void sort(ArrayList<Post> posts, String type)
    {
        if(Objects.equals(type, "date"))
            posts.sort(Comparator.comparing(Post::getDate));
        else if(Objects.equals(type, "distance"))
            posts.sort(Comparator.comparing(Post::getLocation)); // alphabetical
        else if(Objects.equals(type, "popularity"))
            posts.sort(Comparator.comparing(Post::getPopularity).reversed());
    }
}
