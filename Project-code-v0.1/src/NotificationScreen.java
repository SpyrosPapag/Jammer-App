import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class NotificationScreen extends JFrame {
    private JPanel Wrapper;
    private JPanel pagesPanel;
    private JButton listingsButton;
    private JButton chatButton;
    private JButton profileButton;
    private JButton notifsButton;
    private JButton eventsButton;
    private JScrollPane notificationsPanel;
    private JButton clearButton;
    private ArrayList<Notification> notificationsToDisplay;

    public NotificationScreen(Integer user)
    {
        setContentPane(Wrapper);
        setTitle("Notifications");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(375,740);
        setResizable(false);
        setLocationRelativeTo(null);

        // fetch and display users suggested events feed
        viewNotifications(notificationsPanel, user);

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DBManager().clearNotifications(user);
                dispose();
                new NotificationScreen(user);
            }
        });

        listingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Listings(user);
            }
        });

        notifsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new NotificationScreen(user);
            }
        });
//
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
                new Profile(user);
            }
        });

        eventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Events(user);
            }
        });
    }

    private void viewNotifications(JScrollPane notificationsPanel, Integer user)
    {
        // get user`s notifications
        notificationsToDisplay = new DBManager().getNotifications(user);
        if(notificationsToDisplay.isEmpty()) return;

        // display the returned posts
        Notification.displayNotifications(notificationsToDisplay, notificationsPanel);
    }
}
