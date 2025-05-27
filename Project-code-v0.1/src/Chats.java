import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Chats extends JFrame {
    private int userId;
    private JPanel mainPanel;
    private JList<String> chatList;
    private JButton back;
    private JButton requestsButton;
    private DefaultListModel<String> listModel;
    private ArrayList<Integer> chatIdMap = new ArrayList<>();

    public Chats(int userId) {
        this.userId = userId;


        setTitle("Your Chats");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());



        listModel = new DefaultListModel<>();
        chatList = new JList<>(listModel);

        chatList.setCellRenderer(new DefaultListCellRenderer() {
            private final Font biggerFont = new Font("Arial", Font.PLAIN, 18); // bigger font size

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                label.setFont(biggerFont);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding to increase row height

                return label;
            }
        });

        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(chatList);
        add(scrollPane, BorderLayout.CENTER);

        loadChats();

        chatList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = chatList.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        int chatId = chatIdMap.get(index);
                        System.out.println("Selected Chat ID: " + chatId);
                        //ANOIGW TO SYGKEKRIMENO CHAT
                        new ChatScreen(chatId, userId);
                        dispose();
                    }
                }
            }
        });

        back = new JButton("Back");
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(back, BorderLayout.WEST);

        requestsButton = new JButton("Requests");
        topPanel.add(requestsButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        back.addActionListener(e -> {
            dispose();
            new Events(userId);  // your target window
        });


        setVisible(true);
    }

    private void loadChats() {
        DBManager db = new DBManager();
        ArrayList<String[]> chats = db.getChatsForUser(userId);
        for (String[] chatInfo : chats) {
            int chatId = Integer.parseInt(chatInfo[0]);
            String otherUsername = chatInfo[1];
            listModel.addElement("Chat with: " + otherUsername);
            chatIdMap.add(chatId);
        }
    }
}
