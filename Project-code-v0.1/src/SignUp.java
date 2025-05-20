import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SignUp extends JFrame{
    private JPasswordField passwordPF;
    private JButton signUpButton;
    private JTextField usernameTF;
    private JButton loginButton;
    private JPanel Wrapper;
    private JPasswordField confirmPF;

    /* For database connection */
    private static final String JDBC_URL = Main.JDBC_URL;
    private static final String DB_USER = Main.DB_USER;
    private static final String DB_PASSWORD = Main.DB_PASSWORD;

    public static int user;
    public SignUp()
    {
        setContentPane(Wrapper);
        setTitle("Login");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360,740);
        setResizable(false);
        setLocationRelativeTo(null);

        signUpButton.addActionListener(new ActionListener(){@Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameTF.getText();
            char[] passwordChars = passwordPF.getPassword();
            String password = new String(passwordChars);
            char[] confirmPasswordChars = confirmPF.getPassword();
            String confirmpassword = new String(confirmPasswordChars);

            if(!password.equals(confirmpassword))
            {
                JOptionPane.showMessageDialog(null, "Password and Confirm Password don`t match. Please try again.");
                return;
            }

            user = insertUser(username,password);
            if(user > 0)
            {
                usernameTF.setText("");
                passwordPF.setText("");
                confirmPF.setText("");
                JOptionPane.showMessageDialog(null, "SignUp Successful!");
//                    dispose();
//                    new PreferencesPage();
            }
            else
            {
                usernameTF.setText("");
                passwordPF.setText("");
                confirmPF.setText("");
                JOptionPane.showMessageDialog(null,"Username already exists. Please try again.");
            }
        }
        });

        confirmPF.addActionListener(new ActionListener(){@Override
        public void actionPerformed(ActionEvent e) {
            signUpButton.doClick();
        }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Login();
            }
        });
    }

    private int insertUser(String username, String password) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "INSERT INTO user(username, password, push_notifications, listing_notifications, event_notifications, chat_notifications) " +
                               "VALUES(?, ?, 0,0,0,0)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    preparedStatement.executeUpdate();
                    try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                        if (resultSet.next())
                            return resultSet.getInt(1);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return 0; // 0 for invalid
    }
}
