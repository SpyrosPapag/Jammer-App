import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
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
            postsToDisplay.sort(Comparator.comparing(Post::getDate));
            callback.accept(postsToDisplay);
            dispose();
        });


        distanceButton.addActionListener(e -> {
            postsToDisplay.sort(Comparator.comparing(Post::getLocation)); // alphabetical
            callback.accept(postsToDisplay);
            dispose();
        });

        popularityButton.addActionListener(e -> {
            postsToDisplay.sort(Comparator.comparing(Post::getPopularity).reversed());
            callback.accept(postsToDisplay);
            dispose();
        });
    }
}
