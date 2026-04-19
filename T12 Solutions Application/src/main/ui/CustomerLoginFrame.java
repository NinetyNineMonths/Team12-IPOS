package main.ui;

import main.model.User;
import main.service.AuthService;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Login screen for customer users.
 *
 * This class authenticates customer accounts and, where required,
 * forces a password change on first login before allowing access
 * to the main customer interface.
 */

public class CustomerLoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField emailField;
    private JPasswordField passwordField;

    /**
     * Constructs the customer login window.
     */
    public CustomerLoginFrame(AuthService authService) {
        this.authService = authService;

        setTitle("Customer Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 260);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JLabel title = new JLabel("Customer Login", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));

        form.add(new JLabel("Email:"));
        emailField = new JTextField();
        form.add(emailField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        form.add(loginButton);
        form.add(backButton);

        root.add(form, BorderLayout.CENTER);

        loginButton.addActionListener(e -> handleLogin());
        backButton.addActionListener(e -> {
            new WelcomeFrame(authService).setVisible(true);
            dispose();
        });
    }

    /**
     * Validates customer credentials and opens the main UI.
     * If the account is marked as first login, a password change is required.
     */
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        User user = authService.login(email, password);

        if (user == null || !user.isCustomer()) {
            JOptionPane.showMessageDialog(this,
                    "Invalid customer login.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (user.isFirstLogin()) {
            PasswordChange dialog = new PasswordChange(this, authService, user);
            dialog.setVisible(true);

            if (user.isFirstLogin()) {
                return;
            }
        }


        new IPOS_PU_GUI(authService, user).setVisible(true);
        dispose();
    }
}