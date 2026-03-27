package main.service;

import main.model.SalesReport;
import main.model.SalesReportItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private final Connection connection;

    public ReportService(Connection connection) {
        this.connection = connection;
    }

    /* To connect to the database, we have to add these lines to AppLauncher.java:
            Connection conn = DriverManager.getConnection(url, username, password);
            ReportService reportService = new ReportService(conn);

        Once we decide how we're going to access the database, we can add these.
    */

    public SalesReport generateSalesReport(LocalDate startDate, LocalDate endDate)
            throws SQLException {
        
        validateDateRange(startDate, endDate);

        // Once the database is made, we can change some of the names of the tables and columns here. I just made some myself for easier reading.
        String sql = "SELECT " +
                "p.item_id, " +
                "p.description, " +
                "SUM(oi.quantity) AS quantity_sold, " +
                "p.unit_price " +
                "FROM online_orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.item_id = p.item_id " +
                "WHERE o.order_date BETWEEN ? AND ? " +
                "AND o.status = 'COMPLETED' " + // Only counts orders that have been completed
                "GROUP BY p.item_id, p.description, p.unit_price " +
                "ORDER BY p.item_id";
 
        List<SalesReportItem> items = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setObject(1, startDate);
            stmt.setObject(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String itemId = rs.getString("item_id");
                    String description = rs.getString("description");
                    int quantitySold = rs.getInt("quantity_sold");
                    double unitPrice = rs.getDouble("unit_price");

                    items.add(new SalesReportItem(itemId, description, quantitySold, unitPrice));

                }
            }
        }

        int totalUnitsSold = items.stream().mapToInt(SalesReportItem::getQuantitySold).sum();
        double totalRevenue = items.stream().mapToDouble(SalesReportItem::getLineTotal).sum();

        return new SalesReport(startDate, endDate, items, totalUnitsSold, totalRevenue);
        }

    public void generateAdvertisingCampaignsReport(LocalDate startDate, LocalDate endDate) {

    }

    public void generateCampaignEngagementReport(int campaignId) {

    }

    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start date and end date must not be null.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date (" + start + ") must not be after end date (" + end + ").");
        }
    }
}
