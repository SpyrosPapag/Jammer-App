import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JButton sponsorButton;
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
        sponsorButton = new JButton("\uD83E\uDD1D");

        JPanel bottomPanel = new JPanel(new BorderLayout());

        messageListModel = new DefaultListModel<>();
        messageList = new JList<>(messageListModel);


        messageList.setFont(new Font("Arial", Font.PLAIN, 20));

        messageList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component comp;

                if (value instanceof JLabel) {
                    comp = (JLabel) value;
                } else if (value instanceof String) {
                    JTextArea textArea = new JTextArea((String) value);
                    textArea.setWrapStyleWord(true);
                    textArea.setLineWrap(true);
                    textArea.setOpaque(true);
                    textArea.setEditable(false);
                    textArea.setFocusable(false);
                    textArea.setFont(new Font("Arial", Font.PLAIN, 20));
                    textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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

                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setOpaque(false);
                wrapper.add(comp, BorderLayout.CENTER);

                Dimension preferred = comp.getPreferredSize();
                preferred.height = Math.max(30, preferred.height);
                wrapper.setPreferredSize(preferred);

                return wrapper;
            }
        });




        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(addFilesButton);
        leftButtons.add(voiceButton);
        bottomPanel.add(leftButtons, BorderLayout.WEST);


        messageInputField = new JTextField();
        bottomPanel.add(messageInputField, BorderLayout.CENTER);





        sendButton = new JButton("\u27A4");
        JPanel rightButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButton.add(sendButton);
        bottomPanel.add(rightButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
        JPanel rightButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonsPanel.add(sponsorButton);
        rightButtonsPanel.add(searchButton);
        topPanel.add(rightButtonsPanel, BorderLayout.EAST);



        JScrollPane scrollPane = new JScrollPane(messageList);
        add(scrollPane, BorderLayout.CENTER);

        DBManager db = new DBManager();
        int otherUserId = db.getOtherUserId(ChatID, userId);
        String targetUsername = db.getUsernameById(otherUserId);


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
            new Chats(userId);
        });

        sendButton.addActionListener(e -> {
            String message = messageInputField.getText().trim();
            if (!message.isEmpty()) {
                db.insertMessage(userId, ChatID, message);
                Notification.notify(userId, ChatID);
                messageInputField.setText("");
                String senderUsername = db.getUsernameById(userId);
                String formattedMessage = senderUsername + ": " + message;
                messageListModel.addElement(formattedMessage);
            }
        });

        addFilesButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                displayImageMessage(selectedFile);
            }
        });

        searchButton.addActionListener(e -> {

            JDialog searchDialog = new JDialog(this, "Search Messages", true);
            searchDialog.setSize(300, 100);
            searchDialog.setLocationRelativeTo(this);
            searchDialog.setLayout(new BorderLayout());

            JTextField searchField = new JTextField();
            JButton confirmSearchButton = new JButton("\uD83D\uDD0D");

            JPanel searchPanel = new JPanel(new BorderLayout());
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(confirmSearchButton, BorderLayout.EAST);

            searchDialog.add(searchPanel, BorderLayout.CENTER);

            confirmSearchButton.addActionListener(ev -> {
                String keyword = searchField.getText().trim().toLowerCase();
                if (!keyword.isEmpty()) {
                    boolean found = false;
                    for (int i = 0; i < messageListModel.size(); i++) {
                        Object item = messageListModel.get(i);
                        if (item instanceof String && ((String) item).toLowerCase().contains(keyword)) {
                            messageList.setSelectedIndex(i);
                            messageList.ensureIndexIsVisible(i);
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        searchDialog.dispose();
                    }
                }
            });

            searchDialog.setVisible(true);
        });

        sponsorButton.addActionListener(e -> {
            ArrayList<String> posts = db.getPostsByUserId(otherUserId);

            JDialog sponsorDialog = new JDialog(this, "Posts by " + targetUsername, true);
            sponsorDialog.setSize(400, 400);
            sponsorDialog.setLocationRelativeTo(this);

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (String post : posts) {
                JPanel postPanel = new JPanel(new BorderLayout());
                postPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                postPanel.setBackground(new Color(250, 250, 250));

                JLabel postLabel = new JLabel("<html>" + post + "</html>");
                postLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                postPanel.add(postLabel, BorderLayout.CENTER);
                postPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                postPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                postPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        JDialog shopDialog = new JDialog(sponsorDialog, "Event Shop", true);
                        shopDialog.setSize(400, 500);
                        shopDialog.setLocationRelativeTo(sponsorDialog);
                        shopDialog.setLayout(new BorderLayout());

                        DefaultListModel<String> cartModel = new DefaultListModel<>();
                        JList<String> cartList = new JList<>(cartModel);
                        JScrollPane cartScrollPane = new JScrollPane(cartList);
                        cartScrollPane.setPreferredSize(new Dimension(400, 120));

                        JLabel cartLabel = new JLabel("🛒 Cart");
                        cartLabel.setFont(new Font("Arial", Font.BOLD, 16));


                        JButton payButton = new JButton("Pay");
                        payButton.setFont(new Font("Arial", Font.BOLD, 16));


                        JPanel cartControlPanel = new JPanel(new BorderLayout());
                        cartControlPanel.add(cartLabel, BorderLayout.WEST);
                        cartControlPanel.add(payButton, BorderLayout.EAST);


                        JPanel cartPanel = new JPanel(new BorderLayout());
                        cartPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                        cartPanel.add(cartControlPanel, BorderLayout.NORTH);
                        cartPanel.add(cartScrollPane, BorderLayout.CENTER);


                        payButton.addActionListener(ev -> {
                            if (cartModel.isEmpty()) {
                                JOptionPane.showMessageDialog(shopDialog, "Your cart is empty!", "Payment", JOptionPane.WARNING_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(shopDialog, "Thank you for your purchase!", "Payment", JOptionPane.INFORMATION_MESSAGE);
                                cartModel.clear();
                            }
                        });



                        JButton beveragesButton = new JButton("🥤 Beverages");
                        JButton snacksButton = new JButton("🍿 Snacks");
                        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        categoryPanel.add(beveragesButton);
                        categoryPanel.add(snacksButton);


                        JPanel itemsPanel = new JPanel();
                        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
                        JScrollPane itemsScrollPane = new JScrollPane(itemsPanel);


                        String[][] beverages = {
                                {"Φραπέ", "1.50€"},
                                {"Φρέντο Εσπρέσο", "2.00€"},
                                {"Φρέντο Καπουτσίνο", "2.30€"},
                                {"Καφές ελληνικός", "1.20€"},
                                {"Χυμός πορτοκάλι", "2.50€"},
                                {"Νερό εμφιαλωμένο", "0.50€"},
                                {"Ανθρακούχο νερό", "1.20€"},
                                {"Πράσινο τσάι", "1.80€"},
                                {"Τσάι με λεμόνι", "1.70€"},
                                {"Αναψυκτικό τύπου cola", "1.30€"},
                                {"Λεμονάδα", "1.50€"},
                                {"Πορτοκαλάδα", "1.50€"},
                                {"Βυσσινάδα", "1.60€"},
                                {"Ροδάκινο νέκταρ", "2.00€"},
                                {"Κακάο ζεστό", "1.80€"}
                        };

                        String[][] snacks = {
                                {"Κουλούρι Θεσσαλονίκης", "1.00€"},
                                {"Τυρόπιτα", "1.80€"},
                                {"Ζαμπονοτυρόπιτα", "2.00€"},
                                {"Σπανακόπιτα", "2.00€"},
                                {"Κρουασάν σοκολάτας", "1.50€"},
                                {"Μπάρες δημητριακών", "1.30€"},
                                {"Πατατάκια", "1.20€"},
                                {"Ποπ κορν", "1.50€"},
                                {"Κριτσίνια", "1.00€"},
                                {"Ξηροί καρποί", "1.80€"},
                                {"Κουραμπιέδες", "2.50€"},
                                {"Μελομακάρονα", "2.50€"},
                                {"Μπισκότα", "1.30€"},
                                {"Σάντουιτς τοστ", "2.20€"},
                                {"Γιαούρτι με μέλι", "2.70€"}
                        };



                        Runnable loadBeverages = () -> {
                            itemsPanel.removeAll();
                            for (String[] item : beverages) {
                                String name = item[0];
                                String price = item[1];
                                JButton itemButton = new JButton(name + " - " + price);
                                itemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                                itemButton.addActionListener(ev -> {
                                    boolean found = false;
                                    for (int i = 0; i < cartModel.size(); i++) {
                                        String cartItem = cartModel.get(i);
                                        if (cartItem.startsWith(name + " - " + price)) {
                                            found = true;
                                            if (cartItem.matches(".*x\\d+$")) {
                                                int countStart = cartItem.lastIndexOf('x') + 1;
                                                int count = Integer.parseInt(cartItem.substring(countStart));
                                                count++;
                                                cartModel.set(i, name + " - " + price + " x" + count);
                                            } else {
                                                cartModel.set(i, name + " - " + price + " x2");
                                            }
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        cartModel.addElement(name + " - " + price);
                                    }
                                });

                                itemsPanel.add(itemButton);
                                itemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                            }
                            shopDialog.revalidate();
                            shopDialog.repaint();
                        };

                        Runnable loadSnacks = () -> {
                            itemsPanel.removeAll();
                            for (String[] item : snacks) {
                                String name = item[0];
                                String price = item[1];
                                JButton itemButton = new JButton(name + " - " + price);
                                itemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                                itemButton.addActionListener(ev -> {
                                    boolean found = false;
                                    for (int i = 0; i < cartModel.size(); i++) {
                                        String cartItem = cartModel.get(i);
                                        if (cartItem.startsWith(name + " - " + price)) {
                                            found = true;
                                            if (cartItem.matches(".*x\\d+$")) {
                                                int countStart = cartItem.lastIndexOf('x') + 1;
                                                int count = Integer.parseInt(cartItem.substring(countStart));
                                                count++;
                                                cartModel.set(i, name + " - " + price + " x" + count);
                                            } else {

                                                cartModel.set(i, name + " - " + price + " x2");
                                            }
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        cartModel.addElement(name + " - " + price);
                                    }
                                });

                                itemsPanel.add(itemButton);
                                itemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                            }
                            shopDialog.revalidate();
                            shopDialog.repaint();
                        };


                        beveragesButton.addActionListener(e1 -> loadBeverages.run());
                        snacksButton.addActionListener(e2 -> loadSnacks.run());


                        loadBeverages.run();



                        shopDialog.add(categoryPanel, BorderLayout.NORTH);
                        shopDialog.add(itemsScrollPane, BorderLayout.CENTER);
                        shopDialog.add(cartPanel, BorderLayout.SOUTH);
                        shopDialog.setVisible(true);
                    }

                });

                contentPanel.add(postPanel);
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }


            JScrollPane sponsorScrollPane = new JScrollPane(contentPanel);
            sponsorScrollPane.setBorder(null);

            sponsorDialog.add(sponsorScrollPane);
            sponsorDialog.setVisible(true);
        });




        setVisible(true);
    }


    private String fetchUsernameById(int userId) {
        DBManager db = new DBManager();
        return db.getUsernameById(userId);
    }

    private void displayImageMessage(File imageFile) {

        ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());


        Image scaledImage = originalIcon.getImage().getScaledInstance(350, 500, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);


        JLabel imageLabel = new JLabel(scaledIcon);


        messageListModel.addElement(imageLabel);
    }

}


