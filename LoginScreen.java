// @author Akarsh Kiran Gowda
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel loginPanel;

    public LoginScreen() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginPanel = new JPanel(new FlowLayout());
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        add(loginPanel);

        // ActionListener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Check login credentials
                if (username.equals("admin") && password.equals("admin123")) {
                    // Successful login as admin, open the Admin Panel
                    dispose(); // Close the login window
                    JFrame adminFrame = new JFrame("Admin Panel");
                    adminFrame.setContentPane(new AdminPanel().mainPanel); // Access the public mainPanel
                    adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    adminFrame.setSize(600, 400);
                    adminFrame.setVisible(true);
                } else if (username.equals("user") && password.equals("user123")) {
                    // Successful login as user, open the Billing Software
                    dispose(); // Close the login window
                    JFrame userFrame = new JFrame("Billing Software");
                    userFrame.setContentPane(new BillingSoftware().mainPanel);
                    userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    userFrame.setSize(600, 400);
                    userFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });
    }
}
