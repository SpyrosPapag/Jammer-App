import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Notification {
    private int notification_id;
    private int source_id;
    private int source_type;

    public Notification(int notification_id, int source_id, int source_type) {
        this.notification_id = notification_id;
        this.source_id = source_id;
        this.source_type = source_type;
    }

    public static void displayNotifications(ArrayList<Notification> notifications, JScrollPane displayPanel) {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        for (Notification notification : notifications)
        {

            JPanel notificationPanel = new JPanel(new BorderLayout(0, 20));
            notificationPanel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
            notificationPanel.setBackground(Color.lightGray);
            Dimension dims = new Dimension(357, 20);
            notificationPanel.setPreferredSize(dims);
            notificationPanel.setMinimumSize(dims);
            notificationPanel.setMaximumSize(new Dimension(357, 20));


            JLabel textLabel;
            if(notification.source_type == 0){
                textLabel = new JLabel("A person you are following has made a new post!");
                notificationPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e){
                        SwingUtilities.getWindowAncestor(displayPanel).dispose();
                        new PostScreen((new DBManager()).getPost(notification.source_id));
                    }
                });
            }
            else{
                textLabel = new JLabel("A person has sent you a message!");
                notificationPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e){
                        SwingUtilities.getWindowAncestor(displayPanel).dispose();
                        new ChatScreen(notification.source_id, Main.loggeduser);
                    }
                });
            }
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            textLabel.setVerticalAlignment(SwingConstants.CENTER);
            notificationPanel.add(textLabel, BorderLayout.SOUTH);

            container.add(notificationPanel);
            container.add(Box.createVerticalStrut(5));
        }


        displayPanel.setViewportView(container);


        container.revalidate();
        container.repaint();
    }

    public static void notify(Post post){
        DBManager notifManager = new DBManager();
        notifManager.distributeNotifications(post);
    }

    public static void notify(Integer userId, Integer chatId){
        DBManager notifManager = new DBManager();
        notifManager.distributeNotifications(userId, chatId);
    }

    public void push(){

    }
}
