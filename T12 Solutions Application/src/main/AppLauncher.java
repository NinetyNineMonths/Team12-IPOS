package main;

import main.service.AuthService;
import main.ui.WelcomeFrame;
import main.db.DatabaseManager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AppLauncher {

    //Connection conn = DriverManager.getConnection(url, username, password);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            DatabaseManager.initialise();
            AuthService authService = new AuthService();
            WelcomeFrame welcomeFrame = new WelcomeFrame(authService);
            welcomeFrame.setVisible(true);
        });
    }
}