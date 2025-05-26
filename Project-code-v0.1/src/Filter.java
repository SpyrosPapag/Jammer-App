import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class Filter extends JFrame{
    private final Consumer<ArrayList<Post>> callback;
    private JButton dateButton;
    private JButton locationButton;
    private JPanel Wrapper;
    private JButton typeButton;


    public Filter(String screen, ArrayList<Post> postsToDisplay, Consumer<ArrayList<Post>> callback)
    {
        this.callback = callback;
        setContentPane(Wrapper);
        setTitle("Filter");
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360,200);
        setResizable(false);
        setLocationRelativeTo(null);


        dateButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(null,"Enter a date (e.g. 2025-05-24):","Date Input",JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.isEmpty()) {
                ArrayList<Post> filtered = new ArrayList<>();
                for (Post post : postsToDisplay) {
                    if (input.equals(post.getDate()))
                        filtered.add(post);
                }
                callback.accept(filtered);
                dispose();
            }
        });

        locationButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(null,"Enter a location (text):","Location Input",JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.isEmpty()) {
                ArrayList<Post> filtered = new ArrayList<>();
                for (Post post : postsToDisplay) {
                    if (input.equals(post.getLocation()))
                        filtered.add(post);
                }
                callback.accept(filtered);
                dispose();
            }
        });

        typeButton.setVisible(false);

        typeButton.addActionListener(e -> {
            String[] options = {"event", "venue", "lesson", "other"};
            JComboBox<String> comboBox = new JComboBox<>(options);
            JPanel panel = new JPanel();
            panel.add(new JLabel("Select a type:"));
            panel.add(comboBox);

            int result = JOptionPane.showConfirmDialog(null, panel, "Type Selection", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String selectedType = (String) comboBox.getSelectedItem();
                if (selectedType != null && !selectedType.isEmpty()) {
                    ArrayList<Post> filtered = new ArrayList<>();
                    for (Post post : postsToDisplay) {
                        if (selectedType.equals(post.getType())) {
                            filtered.add(post);
                        }
                    }
                    callback.accept(filtered);
                    dispose();
                }
            }
        });


        if(Objects.equals(screen, "listings"))
            typeButton.setVisible(true);
    }
}
