package main;

import main.service.AuthService;
import main.ui.WelcomeFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class AppLauncher {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService();
            WelcomeFrame welcomeFrame = new WelcomeFrame(authService);
            welcomeFrame.setVisible(true);
        });
    }
}