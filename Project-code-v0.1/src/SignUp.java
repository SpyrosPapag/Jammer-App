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

            DBManager signUpManager = new DBManager();
            user = signUpManager.insertUser(username,password);
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
}
