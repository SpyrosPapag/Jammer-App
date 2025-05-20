import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame{
    private JPanel Wrapper;
    private JTextField usernameTF;
    private JPasswordField passwordPF;
    private JButton loginButton;
    private JButton signUpButton;


    /* For database connection */
    private static final String JDBC_URL = Main.JDBC_URL;
    private static final String DB_USER = Main.DB_USER;
    private static final String DB_PASSWORD = Main.DB_PASSWORD;

    public static int user;
    public Login()
    {
        setContentPane(Wrapper);
        setTitle("Login");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360,740);
        setResizable(false);
        setLocationRelativeTo(null);

        loginButton.addActionListener(new ActionListener(){@Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameTF.getText();
                char[] passwordChars = passwordPF.getPassword();
                String password = new String(passwordChars);

                user = credentialsCheck(username,password);
                if(user > 0)
                {
                    usernameTF.setText("");
                    passwordPF.setText("");
                    JOptionPane.showMessageDialog(null, "Login Successful!");
//                    dispose();
//                    new FeedPage();
                }
                else
                {
                    usernameTF.setText("");
                    passwordPF.setText("");
                    JOptionPane.showMessageDialog(null,"Invalid username or password. Please try again.");
                }
            }
        });

        passwordPF.addActionListener(new ActionListener(){@Override
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick();
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SignUp();
            }
        });
    }

    private int credentialsCheck(String username, String password) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "SELECT user_id FROM user WHERE username = ? AND password = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        if(resultSet.next()) // Returns true if the result set is not empty (valid credentials)
                            return resultSet.getInt(1); // return user_id
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return 0; // 0 for invalid
    }
}