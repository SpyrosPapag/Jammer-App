import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NotificationSettings extends JFrame {
    private JPanel Wrapper;
    private JCheckBox pushBox;
    private JCheckBox eventBox;
    private JCheckBox listingBox;
    private JCheckBox chatBox;

    public NotificationSettings() {
        setContentPane(Wrapper);
        setTitle("Filter");
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360,200);
        setResizable(false);
        setLocationRelativeTo(null);

        pushBox.setSelected(Main.pushNotif);
        eventBox.setSelected(Main.eventNotif);
        listingBox.setSelected(Main.listingNotif);
        chatBox.setSelected(Main.chatNotif);

        pushBox.addItemListener(e -> {
                Main.pushNotif = pushBox.isSelected();
        });

        eventBox.addItemListener(e -> {
            Main.eventNotif = eventBox.isSelected();
        });

        listingBox.addItemListener(e -> {
            Main.listingNotif = listingBox.isSelected();
        });

        chatBox.addItemListener(e -> {
            Main.chatNotif = chatBox.isSelected();
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e){
                new DBManager().updateNotificationSettings();
            }
        });
    }
}
