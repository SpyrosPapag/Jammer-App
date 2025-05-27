import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class ChatScreen extends JFrame {
    private JButton addFilesButton;
    private JPanel mainPanel;
    private JTextField messageInputField;
    private JButton sendButton;
    private JButton voiceButton;
    private JButton backButton;
    private JTextField usernameField;
    private JList<Object> messageList;
    private JButton searchButton;
    private DefaultListModel<Object> messageListModel;

    public ChatScreen(Integer ChatID, Integer userId) {
        setTitle("Chat");
        setSize(400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        backButton = new JButton("Back");
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);

        addFilesButton = new JButton("\uD83D\uDCE9");
        voiceButton = new JButton("\uD83C\uDFA4");
        searchButton = new JButton("\uD83D\uDD0D");

        JPanel bottomPanel = new JPanel(new BorderLayout());

        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);

        //GRAMATOSEIRA
        messageList.setFont(new Font("Arial", Font.PLAIN, 20));
        //messageList.setFixedCellHeight(30);

        messageList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component comp;

                if (value instanceof JLabel) {
                    // Image message — return as is
                    comp = (JLabel) value;
                } else if (value instanceof String) {
                    // Text message — use JTextArea for wrapping
                    JTextArea textArea = new JTextArea((String) value);
                    textArea.setWrapStyleWord(true);
                    textArea.setLineWrap(true);
                    textArea.setOpaque(true);
                    textArea.setEditable(false);
                    textArea.setFocusable(false);
                    textArea.setFont(new Font("Arial", Font.PLAIN, 20));
                    textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Optional padding

                    // Set background/foreground colors for selection
                    if (isSelected) {
                        textArea.setBackground(list.getSelectionBackground());
                        textArea.setForeground(list.getSelectionForeground());
                    } else {
                        textArea.setBackground(list.getBackground());
                        textArea.setForeground(list.getForeground());
                    }

                    comp = textArea;
                } else {
                    comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }

                // Wrap in a panel to enforce minimum height
                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setOpaque(false); // Let inner component control bg
                wrapper.add(comp, BorderLayout.CENTER);

                // Calculate preferred height (minimum 30)
                Dimension preferred = comp.getPreferredSize();
                preferred.height = Math.max(30, preferred.height);
                wrapper.setPreferredSize(preferred);

                return wrapper;
            }
        });



        //ARISTERI
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(addFilesButton);
        leftButtons.add(voiceButton);
        bottomPanel.add(leftButtons, BorderLayout.WEST);

        // KENTRO
        messageInputField = new JTextField();
        bottomPanel.add(messageInputField, BorderLayout.CENTER);




        //DEKSIA
        sendButton = new JButton("\u27A4");
        JPanel rightButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButton.add(sendButton);
        bottomPanel.add(rightButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
        topPanel.add(searchButton, BorderLayout.EAST);

        //JLIST EDW
        JScrollPane scrollPane = new JScrollPane(messageList);
        add(scrollPane, BorderLayout.CENTER);

        DBManager db = new DBManager();
        int otherUserId = db.getOtherUserId(ChatID, userId);
        String targetUsername = db.getUsernameById(otherUserId);

        // MEGALITERO FONT
        JLabel usernameLabel = new JLabel(targetUsername, SwingConstants.CENTER);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(usernameLabel, BorderLayout.CENTER);


        add(topPanel, BorderLayout.NORTH);

        ArrayList<String[]> messages = db.getMessagesForChat(ChatID);
        for (String[] msg : messages) {
            String senderUsername = msg[0];
            String content = msg[1];
            messageListModel.addElement(senderUsername + ": " + content);
        }



        backButton.addActionListener(e -> {
            dispose();
            new Chats(userId);  // your target window
        });

        sendButton.addActionListener(e -> {
            String message = messageInputField.getText().trim();
            if (!message.isEmpty()) {
                db.insertMessage(userId, ChatID, message);
                messageInputField.setText(""); //CLEAR
                String senderUsername = db.getUsernameById(userId);  //GET USERNAME
                String formattedMessage = senderUsername + ": " + message;
                messageListModel.addElement(formattedMessage);
            }
        });

        addFilesButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            // Only allow image files
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                displayImageMessage(selectedFile);
            }
        });

        searchButton.addActionListener(e -> {
            // Create a dialog
            JDialog searchDialog = new JDialog(this, "Search Messages", true);
            searchDialog.setSize(300, 100);
            searchDialog.setLocationRelativeTo(this);
            searchDialog.setLayout(new BorderLayout());

            // Components
            JTextField searchField = new JTextField();
            JButton confirmSearchButton = new JButton("\uD83D\uDD0D"); // 🔍

            // Panel to hold field + button
            JPanel searchPanel = new JPanel(new BorderLayout());
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(confirmSearchButton, BorderLayout.EAST);

            searchDialog.add(searchPanel, BorderLayout.CENTER);

            // Action for confirmSearchButton
            confirmSearchButton.addActionListener(ev -> {
                String keyword = searchField.getText().trim().toLowerCase();
                if (!keyword.isEmpty()) {
                    boolean found = false;
                    for (int i = 0; i < messageListModel.size(); i++) {
                        Object item = messageListModel.get(i);
                        if (item instanceof String && ((String) item).toLowerCase().contains(keyword)) {
                            messageList.setSelectedIndex(i); // highlight the match
                            messageList.ensureIndexIsVisible(i); // scroll to it
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        searchDialog.dispose(); // close popup only if match is found
                    }
                }
            });

            searchDialog.setVisible(true);
        });



        setVisible(true);
    }


    private String fetchUsernameById(int userId) {
        DBManager db = new DBManager();
        return db.getUsernameById(userId);
    }

    private void displayImageMessage(File imageFile) {
        // Step 1: Load the image from the file path
        ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());

        // Step 2: Resize the image to fit better in the chat (e.g., 200x200 px)
        Image scaledImage = originalIcon.getImage().getScaledInstance(350, 500, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Step 3: Create a JLabel to hold the scaled image
        JLabel imageLabel = new JLabel(scaledIcon);

        // Step 4: Add the label to the list model
        messageListModel.addElement(imageLabel);
    }

}


