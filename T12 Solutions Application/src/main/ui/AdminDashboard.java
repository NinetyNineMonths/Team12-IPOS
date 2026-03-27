package main.ui;

import main.model.User;
import main.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final User user;
    private final AuthService authService;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public AdminDashboard(User user, AuthService authService) {
        this.user = user;
        this.authService = authService;

        setTitle("T-12 Applications Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        JLabel header = new JLabel("IPOS-PU - Admin Portal", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setBorder(new EmptyBorder(15, 10, 15, 10));
        root.add(header, BorderLayout.NORTH);

        JPanel sideMenu = new JPanel(new GridLayout(0, 1, 8, 8));
        sideMenu.setBorder(new EmptyBorder(10, 10, 10, 10));
        sideMenu.setPreferredSize(new Dimension(220, 0));

        JButton homeButton = new JButton("Home");
        JButton promotionsAdminButton = new JButton("Promotions Admin");
        JButton reportsButton = new JButton("Reports");
        JButton logoutButton = new JButton("Logout");

        sideMenu.add(homeButton);
        sideMenu.add(promotionsAdminButton);
        sideMenu.add(reportsButton);
        sideMenu.add(logoutButton);

        root.add(sideMenu, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(createPlaceholderPanel("Promotions Admin screen"), "PROMOTIONS_ADMIN");
        contentPanel.add(createPlaceholderPanel("Reports screen"), "REPORTS");

        root.add(contentPanel, BorderLayout.CENTER);

        homeButton.addActionListener(e -> cardLayout.show(contentPanel, "HOME"));
        promotionsAdminButton.addActionListener(e -> cardLayout.show(contentPanel, "PROMOTIONS_ADMIN"));
        reportsButton.addActionListener(e -> cardLayout.show(contentPanel, "REPORTS"));

        logoutButton.addActionListener(e -> {
            new WelcomeFrame(authService).setVisible(true);
            dispose();
        });
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Welcome, " + user.getEmail());
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setText("""
                You are logged in as an admin.""");

        panel.add(title, BorderLayout.NORTH);
        panel.add(info, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPlaceholderPanel(String message) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(message + " coming next");
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        panel.add(label);
        return panel;
    }
}