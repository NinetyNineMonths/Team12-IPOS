package main.ui;

import main.service.ReportService;
import main.db.DatabaseManager;
import main.model.*;
import main.ui.DatabaseViewer;

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
        JButton engagementBtn = new JButton("Campaign Engagement");
        JButton viewDbBtn = new JButton("View Users DB");

        topPanel.add(salesBtn);
        topPanel.add(campaignBtn);
        topPanel.add(createCampaignBtn);
        topPanel.add(engagementBtn);
        topPanel.add(viewDbBtn);

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
        viewDbBtn.addActionListener(e -> {
            new DatabaseViewer().setVisible(true);
        });
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