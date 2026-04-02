package main.ui;

import main.service.ReportService;
import main.db.DatabaseManager;
import main.model.*;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.time.LocalDate;

public class AdminDashboard extends JFrame {

    private JTextArea outputArea;
    private ReportService reportService;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            Connection conn = DatabaseManager.getConnection();
            reportService = new ReportService(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed");
            return;
        }

        // top button panel
        JPanel topPanel = new JPanel();

        JButton salesBtn = new JButton("Sales Report");
        JButton campaignBtn = new JButton("Campaign Report");
        JButton createCampaignBtn = new JButton("Create Campaign");
        topPanel.add(createCampaignBtn);
        JButton engagementBtn = new JButton("Campaign Engagement");

        topPanel.add(salesBtn);
        topPanel.add(campaignBtn);
        topPanel.add(engagementBtn);

        add(topPanel, BorderLayout.NORTH);

        // ouput
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // action buttons

        salesBtn.addActionListener(e -> generateSalesReport());
        campaignBtn.addActionListener(e -> generateCampaignReport());
        createCampaignBtn.addActionListener(e -> openCreateCampaignDialog());
        engagementBtn.addActionListener(e -> generateEngagementReport());
    }

    private void generateSalesReport() {
        try {
            LocalDate start = LocalDate.now().minusDays(30);
            LocalDate end = LocalDate.now();

            SalesReport report = reportService.generateSalesReport(start, end);
            outputArea.setText(report.toString());

        } catch (Exception e) {
            outputArea.setText("Error generating sales report:\n" + e.getMessage());
        }
    }

    private void generateCampaignReport() {
        try {
            LocalDate start = LocalDate.now().minusDays(30);
            LocalDate end = LocalDate.now();

            CampaignsReport report = reportService.generateCampaignsReport(start, end);
            outputArea.setText(report.toString());

        } catch (Exception e) {
            outputArea.setText("Error generating campaign report:\n" + e.getMessage());
        }
    }

    private void openCreateCampaignDialog() {

        JTextField idField = new JTextField();
        JTextField startField = new JTextField("2026-04-01");
        JTextField endField = new JTextField("2026-04-30");
        JTextField discountTypeField = new JTextField(" ");

        JPanel panel = new JPanel(new GridLayout(0, 1));

        panel.add(new JLabel("Campaign ID:"));
        panel.add(idField);

        panel.add(new JLabel("Start DateTime (YYYY-MM-DD):"));
        panel.add(startField);

        panel.add(new JLabel("End DateTime (YYYY-MM-DD):"));
        panel.add(endField);

        panel.add(new JLabel("Discount Type:"));
        panel.add(discountTypeField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Create Campaign",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText().trim();

                java.time.LocalDate startDate =
                        java.time.LocalDate.parse(startField.getText().trim());

                java.time.LocalDate endDate =
                        java.time.LocalDate.parse(endField.getText().trim());

                if (endDate.isBefore(startDate)) {
                    JOptionPane.showMessageDialog(this,
                            "End date must be after start date.",
                            "Invalid Dates",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.time.LocalDateTime start = startDate.atStartOfDay();
                java.time.LocalDateTime end = endDate.atTime(23, 59);

                String discountType = discountTypeField.getText().trim();

                // TEMP: create campaign object (no DB yet)
                main.model.Campaign campaign = new main.model.Campaign(
                        id,
                        start,
                        end,
                        discountType,
                        new java.util.ArrayList<>(),
                        false
                );

                JOptionPane.showMessageDialog(this,
                        "Campaign Created:\n" + campaign.toString());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input.\nCheck date format.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateEngagementReport() {
        String campaignId = JOptionPane.showInputDialog(this, "Enter Campaign ID:");

        if (campaignId == null || campaignId.trim().isEmpty()) return;

        try {
            CampaignEngagementReport report =
                    reportService.generateCampaignEngagementReport(campaignId);

            outputArea.setText(report.toString());

        } catch (Exception e) {
            outputArea.setText("Error:\n" + e.getMessage());
        }
    }
}


//package main.ui;
//
//import main.model.User;
//import main.service.AuthService;
//
//import javax.swing.*;
//import java.awt.*;
//import java.time.LocalDateTime;
//
//public class AdminDashboard extends JFrame {
//
//    private final User currentUser;
//    private final AuthService authService;
//    private JTabbedPane mainTabs;
//
//    public AdminDashboard(User currentUser, AuthService authService) {
//        this.currentUser = currentUser;
//        this.authService = authService;
//
//        setTitle("IPOS-PU Admin Dashboard");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(1100, 700);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout());
//
//        createHeader();
//
//        mainTabs = new JTabbedPane();
//        mainTabs.addTab("Home", createHomePanel());
//        mainTabs.addTab("Campaigns", createCampaignsPanel());
//        mainTabs.addTab("Reports", createReportsPanel());
//        mainTabs.addTab("Commercial Applications", createCommercialApplicationsPanel());
//
//        add(mainTabs, BorderLayout.CENTER);
//
//        JLabel status = new JLabel(" Admin Portal • Connected to subsystem services • " + LocalDateTime.now().toLocalDate());
//        status.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
//        add(status, BorderLayout.SOUTH);
//    }
//
//    private void createHeader() {
//        JPanel header = new JPanel(new BorderLayout());
//        header.setBackground(new Color(0, 102, 204));
//        header.setPreferredSize(new Dimension(0, 60));
//
//        JLabel title = new JLabel("IPOS-PU Admin - T12 Solutions");
//        title.setFont(new Font("Arial", Font.BOLD, 28));
//        title.setForeground(Color.WHITE);
//        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
//        header.add(title, BorderLayout.WEST);
//
//        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        rightPanel.setOpaque(false);
//
//        JButton userButton = new JButton("Admin: " + currentUser.getEmail());
//        userButton.setEnabled(false);
//        rightPanel.add(userButton);
//
//        JButton logoutButton = new JButton("Logout");
//        logoutButton.addActionListener(e -> {
//            new IPOS_PU_GUI(authService, null).setVisible(true);
//            dispose();
//        });
//        rightPanel.add(logoutButton);
//
//        header.add(rightPanel, BorderLayout.EAST);
//        add(header, BorderLayout.NORTH);
//    }
//
//    private JPanel createHomePanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        JTextArea info = new JTextArea();
//        info.setEditable(false);
//        info.setFont(new Font("Arial", Font.PLAIN, 15));
//        info.setText("""
//                Welcome to the IPOS-PU Admin Dashboard.
//                """);
//
//        panel.add(info, BorderLayout.CENTER);
//        return panel;
//    }
//
//    private JPanel createCampaignsPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        JTextArea area = new JTextArea();
//        area.setEditable(false);
//        area.setFont(new Font("Arial", Font.PLAIN, 14));
//        area.setText("""
//                Campaign management area.
//                """);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        JButton refreshButton = new JButton("Refresh Campaigns");
//        refreshButton.addActionListener(e ->
//                JOptionPane.showMessageDialog(this,
//                        "Campaign service wiring is the next step.")
//        );
//
//        JButton createButton = new JButton("Create Campaign");
//        createButton.addActionListener(e ->
//                JOptionPane.showMessageDialog(this,
//                        "Create campaign form can be added once PromotionService methods are confirmed.")
//        );
//
//        buttonPanel.add(refreshButton);
//        buttonPanel.add(createButton);
//
//        panel.add(buttonPanel, BorderLayout.NORTH);
//        panel.add(new JScrollPane(area), BorderLayout.CENTER);
//
//        return panel;
//    }
//
//    private JPanel createReportsPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        JTextArea area = new JTextArea();
//        area.setEditable(false);
//        area.setFont(new Font("Arial", Font.PLAIN, 14));
//        area.setText("""
//                Reports area.
//                """);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//
//        JButton salesReportButton = new JButton("Sales Report");
//        salesReportButton.addActionListener(e ->
//                JOptionPane.showMessageDialog(this,
//                        "Sales report service wiring is the next step.")
//        );
//
//        JButton campaignReportButton = new JButton("Campaign Report");
//        campaignReportButton.addActionListener(e ->
//                JOptionPane.showMessageDialog(this,
//                        "Campaign report service wiring is the next step.")
//        );
//
//        buttonPanel.add(salesReportButton);
//        buttonPanel.add(campaignReportButton);
//
//        panel.add(buttonPanel, BorderLayout.NORTH);
//        panel.add(new JScrollPane(area), BorderLayout.CENTER);
//
//        return panel;
//    }
//
//    private JPanel createCommercialApplicationsPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        JTextArea area = new JTextArea();
//        area.setEditable(false);
//        area.setFont(new Font("Arial", Font.PLAIN, 14));
//        area.setText("""
//                Commercial applications area.
//                """);
//
//        JButton refreshButton = new JButton("Refresh Applications");
//        refreshButton.addActionListener(e ->
//                JOptionPane.showMessageDialog(this,
//                        "Commercial application service wiring is the next step.")
//        );
//
//        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        top.add(refreshButton);
//
//        panel.add(top, BorderLayout.NORTH);
//        panel.add(new JScrollPane(area), BorderLayout.CENTER);
//
//        return panel;
//    }
//}