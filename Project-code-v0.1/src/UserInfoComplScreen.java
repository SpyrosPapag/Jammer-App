import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInfoComplScreen extends JFrame {
    private JPanel Wrapper;
    private JPasswordField passwordPF;
    private JButton createProfileButton;
    private JTextField mailTF;
    private JPasswordField confirmPF;
    private JButton loginButton;

    public static int user;

    public UserInfoComplScreen()
    {
        setContentPane(Wrapper);
        setTitle("User Information");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360,740);
        setResizable(false);
        setLocationRelativeTo(null);

        createProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mail = mailTF.getText().trim();
                char[] passwordChars = passwordPF.getPassword();
                String password = new String(passwordChars);
                char[] confirmPasswordChars = confirmPF.getPassword();
                String confirmpassword = new String(confirmPasswordChars);

                if (mail.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be completed.");
                    return;
                }

                if (!password.equals(confirmpassword)) {
                    JOptionPane.showMessageDialog(null, "Password and Confirm Password don`t match. Please try again.");
                    return;
                }

                DBManager signUpManager = new DBManager();
                user = signUpManager.insertUser(mail, password);
                if (user > 0) {
                    mailTF.setText("");
                    passwordPF.setText("");
                    confirmPF.setText("");
                    JOptionPane.showMessageDialog(null, "SignUp Successful!");
                    dispose();
                    new ProfileInfoCompletion(user); // Show next interface
                } else {
                    mailTF.setText("");
                    passwordPF.setText("");
                    confirmPF.setText("");
                    JOptionPane.showMessageDialog(null, "A user has already signed up with that mail.");
                }
            }
        });

        confirmPF.addActionListener(new ActionListener(){@Override
        public void actionPerformed(ActionEvent e) {
            createProfileButton.doClick();
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
