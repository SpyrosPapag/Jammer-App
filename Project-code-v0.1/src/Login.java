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

                DBManager loginManager = new DBManager();
                user = loginManager.credentialsCheck(username,password);
                if(user > 0)
                {
                    usernameTF.setText("");
                    passwordPF.setText("");
                    Main.loggeduser = user;
                    JOptionPane.showMessageDialog(null, "Login Successful!");
                    dispose();
                    new Events(user);
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
                new UserInfoComplScreen();
            }
        });
    }
}
