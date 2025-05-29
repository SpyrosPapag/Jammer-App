import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class EditCritiquesScreen extends JFrame {
    private JButton viewCritiquesButton;
    private JButton addCritiqueButton;
    private JButton backButton;
    private JButton deleteCritiqueButton;
    private JPanel Wrapper;
    private JPanel viewCritiques;
    private JPanel addCritique;
    private JPanel controls;

    public EditCritiquesScreen(Integer user) {
        setContentPane(Wrapper);
        setTitle("Edit Critiques Screen");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375, 740);
        setResizable(false);
        setLocationRelativeTo(null);

        viewCritiquesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UsersPostList();
                dispose(); 
            }
        });
        addCritiqueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PostList();
                dispose(); 
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Profile(user);
                dispose();
            }
        });
        deleteCritiqueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PostList();
                dispose(); 
            }
        });
    }


}

