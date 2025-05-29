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
            new Chats(userId);
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
            private final Font biggerFont = new Font("Arial", Font.PLAIN, 24);

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                label.setFont(biggerFont);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                return label;
            }
        });

        requestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && requestList.getSelectedIndex() != -1) {

                acceptButton.setVisible(true);
                declineButton.setVisible(true);




            } else {

                acceptButton.setVisible(false);
                declineButton.setVisible(false);
            }
        });

        acceptButton.addActionListener(e -> {
            int selectedIndex = requestList.getSelectedIndex();
            if (selectedIndex != -1) {
                String senderUsername = listModel.getElementAt(selectedIndex);
                DBManager db = new DBManager();



                int senderId = db.getUserIdByUsername(senderUsername);
                if (senderId != -1) {
                    db.acceptChatRequest(senderId, userId);
                    refreshRequestList();
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
                    db.declineChatRequest(senderId, userId);
                    refreshRequestList();
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