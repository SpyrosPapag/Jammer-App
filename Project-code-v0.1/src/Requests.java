import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Requests {
    private JPanel mainPanel;
    private JButton back;
    private JList<String> requestList;
    private JButton declineButton;
    private JButton acceptButton;
    private DefaultListModel<String> listModel;
    private int userId;

    public Requests(int userId) {
        this.userId = userId;

        JFrame frame = new JFrame("Chat Requests");
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        back = new JButton("Back");
        acceptButton = new JButton("Accept");
        declineButton = new JButton("Decline");
        acceptButton.setVisible(false);
        declineButton.setVisible(false);
        listModel = new DefaultListModel<>();
        requestList = new JList<>(listModel);

        acceptButton.setBackground(new Color(0, 200, 0));
        acceptButton.setForeground(Color.WHITE);

        declineButton.setBackground(new Color(200, 0, 0));
        declineButton.setForeground(Color.WHITE);

        acceptButton.setOpaque(true);
        acceptButton.setBorderPainted(false);

        declineButton.setOpaque(true);
        declineButton.setBorderPainted(false);


        loadRequests();

        back.addActionListener(e -> {
            frame.dispose();
            new Chats(userId);  // Navigate back to Chats
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(back);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(requestList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(acceptButton);
        bottomPanel.add(declineButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);



        requestList.setCellRenderer(new DefaultListCellRenderer() {
            private final Font biggerFont = new Font("Arial", Font.PLAIN, 24); // Adjust size here

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                label.setFont(biggerFont);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for height
                return label;
            }
        });

        requestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && requestList.getSelectedIndex() != -1) {
                // A row is selected
                acceptButton.setVisible(true);
                declineButton.setVisible(true);

                // Optional: enable instead of showing
                // acceptButton.setEnabled(true);
                // declineButton.setEnabled(true);
            } else {
                // No row selected
                acceptButton.setVisible(false);
                declineButton.setVisible(false);
            }
        });

        acceptButton.addActionListener(e -> {
            int selectedIndex = requestList.getSelectedIndex();
            if (selectedIndex != -1) {
                String senderUsername = listModel.getElementAt(selectedIndex);
                DBManager db = new DBManager();

                // You need senderId and recipientId; assuming recipient is current user (userId)
                // So first get senderId from username
                int senderId = db.getUserIdByUsername(senderUsername);
                if (senderId != -1) {
                    db.acceptChatRequest(senderId, userId); // Accept request and create chat
                    refreshRequestList(); // method to reload and refresh the JList
                }
            }
        });

        declineButton.addActionListener(e -> {
            int selectedIndex = requestList.getSelectedIndex();
            if (selectedIndex != -1) {
                String senderUsername = listModel.getElementAt(selectedIndex);
                DBManager db = new DBManager();

                int senderId = db.getUserIdByUsername(senderUsername);
                if (senderId != -1) {
                    db.declineChatRequest(senderId, userId); // Set status to Declined
                    refreshRequestList(); // Reload the list
                }
            }
        });



        frame.setVisible(true);
    }

    private void loadRequests() {
        DBManager db = new DBManager();
        ArrayList<String> pendingUsernames = db.getPendingRequestUsernames(userId);

        for (String username : pendingUsernames) {
            listModel.addElement(username);
        }
    }

    private void refreshRequestList() {
        listModel.clear();
        DBManager db = new DBManager();
        ArrayList<String> pendingUsernames = db.getPendingRequestUsernames(userId);
        for (String username : pendingUsernames) {
            listModel.addElement(username);
        }
        acceptButton.setVisible(false);
        declineButton.setVisible(false);
    }

}