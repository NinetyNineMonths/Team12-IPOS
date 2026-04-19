package main.service;

import main.api.PUCommsAPI;
import main.db.DatabaseManager;
import main.implementation.PUCommsAPIImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("SqlResolve")

/**
 * Service responsible for processing customer orders.
 *
 * This class validates checkout requests, records orders and payments,
 * updates order history, checks loyalty discounts, and sends
 * order confirmation and tracking emails.
 */

public class OrderService {

    private final PUCommsAPI commsApi;

    /**
     * Constructs the order service using the default communications implementation.
     */
    public OrderService() {
        this(new PUCommsAPIImpl());
    }

    /**
     * Constructs the order service with a supplied communications API.
     */
    public OrderService(PUCommsAPI commsApi) {
        this.commsApi = commsApi;
    }

    /**
     * Processes a complete checkout request, including validation,
     * payment authorisation, order persistence, merchant propagation,
     * and confirmation email sending.
     */
    public CheckoutResult checkoutOrder(String userEmail,
                                        List<OrderLine> items,
                                        String addressLine1,
                                        String addressLine2,
                                        String cardNumber,
                                        boolean nonCommercialMember) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            return CheckoutResult.failure("User email is required.");
        }
        if (items == null || items.isEmpty()) {
            return CheckoutResult.failure("At least one order item is required.");
        }
        if (addressLine1 == null || addressLine1.trim().isEmpty()) {
            return CheckoutResult.failure("Delivery address is required.");
        }

        for (OrderLine line : items) {
            if (!line.isValid()) {
                return CheckoutResult.failure("Order item details are invalid.");
            }
        }

        String orderId = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
        double subtotal = calculateSubtotal(items);
        boolean discountApplied = nonCommercialMember && qualifiesForTenthOrderDiscount(userEmail);
        double finalTotal = discountApplied ? subtotal * 0.90 : subtotal;

        boolean paymentAuthorised = authorisePayment(orderId, finalTotal, cardNumber);
        String paymentStatus = paymentAuthorised ? "COMPLETED" : "FAILED";
        String orderStatus = paymentAuthorised ? "Received" : "Payment Failed";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                insertOrder(conn, orderId, userEmail, items, orderStatus, finalTotal);
                insertOrderItems(conn, orderId, items);
                insertPaymentRecord(conn, orderId, userEmail, addressLine1, addressLine2, paymentStatus);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return CheckoutResult.failure("Failed to persist order: " + e.getMessage());
        }

        if (!paymentAuthorised) {
            return CheckoutResult.failureWithOrder(
                    orderId,
                    finalTotal,
                    "Payment was declined."
            );
        }

        boolean propagated = propagateSaleToMerchant(orderId, userEmail, addressLine1, addressLine2, items);
        sendTrackingEmail(userEmail, orderId, finalTotal, discountApplied);

        return CheckoutResult.success(orderId, finalTotal, discountApplied, propagated);
    }

    /**
     * Updates the status of an existing order.
     */
    public boolean updateOrderStatus(String orderId, String newStatus) {
        if (orderId == null || orderId.trim().isEmpty()) return false;
        if (newStatus == null || newStatus.trim().isEmpty()) return false;

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.trim());
            ps.setString(2, orderId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Retrieves the current status of a specific order for a given user.
     */
    public String getOrderStatus(String userEmail, String orderId) {
        if (userEmail == null || userEmail.trim().isEmpty()) return null;
        if (orderId == null || orderId.trim().isEmpty()) return null;

        String sql = "SELECT status FROM orders WHERE user_email = ? AND order_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userEmail.trim().toLowerCase());
            ps.setString(2, orderId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("status");
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    /**
     * Retrieves a summary list of all orders placed by a specific user.
     */
    public List<OrderSummary> getOrdersForUser(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) return Collections.emptyList();

        String sql = """
            SELECT order_id, order_date, item_count, status, total_amount
            FROM orders
            WHERE user_email = ?
            ORDER BY order_date DESC
        """;

        List<OrderSummary> summaries = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userEmail.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summaries.add(new OrderSummary(
                            rs.getString("order_id"),
                            rs.getString("order_date"),
                            rs.getInt("item_count"),
                            rs.getString("status"),
                            rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException ignored) {
        }
        return summaries;
    }

    /**
     * Retrieves all individual order lines for a specific order.
     */
    public List<OrderLine> getOrderItems(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) return Collections.emptyList();

        String sql = """
            SELECT product_id, product_name, quantity, unit_price, line_total, campaign_id
            FROM order_items
            WHERE order_id = ?
            ORDER BY product_id
        """;

        List<OrderLine> items = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderLine(
                            rs.getString("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getString("campaign_id")
                    ));
                }
            }
        } catch (SQLException ignored) {
        }
        return items;
    }

    /**
     * Checks whether the user qualifies for the 10th-order non-commercial member discount.
     */
    public boolean qualifiesForTenthOrderDiscount(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) return false;

        String sql = """
            SELECT COUNT(*) AS completed_count
            FROM orders
            WHERE user_email = ?
              AND status IN ('Received', 'Dispatched', 'Delivered')
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userEmail.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                int completed = rs.getInt("completed_count");
                return (completed + 1) % 10 == 0;
            }
        } catch (SQLException ignored) {
            return false;
        }
    }

    /**
     * Inserts the main order record into the orders table.
     */
    private void insertOrder(Connection conn,
                             String orderId,
                             String userEmail,
                             List<OrderLine> items,
                             String status,
                             double totalAmount) throws SQLException {
        String sql = """
            INSERT INTO orders (order_id, user_email, order_date, item_count, status, total_amount)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            ps.setString(2, userEmail.trim().toLowerCase());
            ps.setString(3, LocalDateTime.now().toString());
            ps.setInt(4, items.size());
            ps.setString(5, status);
            ps.setDouble(6, totalAmount);
            ps.executeUpdate();
        }
    }

    /**
     * Inserts all order line records linked to a specific order.
     */
    private void insertOrderItems(Connection conn, String orderId, List<OrderLine> items) throws SQLException {
        String sql = """
            INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, line_total, campaign_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderLine line : items) {
                ps.setString(1, orderId);
                ps.setString(2, line.productId());
                ps.setString(3, line.productName());
                ps.setInt(4, line.quantity());
                ps.setDouble(5, line.unitPrice());
                ps.setDouble(6, line.lineTotal());
                ps.setString(7, line.campaignId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Inserts a payment record linked to the order.
     */
    private void insertPaymentRecord(Connection conn,
                                     String orderId,
                                     String userEmail,
                                     String addressLine1,
                                     String addressLine2,
                                     String paymentStatus) throws SQLException {
        String sql = """
            INSERT INTO payments (payment_id, order_id, user_email, address_line_1, address_line_2, payment_status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "PAY-" + orderId);
            ps.setString(2, orderId);
            ps.setString(3, userEmail.trim().toLowerCase());
            ps.setString(4, addressLine1.trim());
            ps.setString(5, addressLine2 == null ? null : addressLine2.trim());
            ps.setString(6, paymentStatus);
            ps.executeUpdate();
        }
    }

    /**
     * Authorises payment using the CommsAPI.
     */
    private boolean authorisePayment(String orderId, double amount, String cardNumber) {
        if (commsApi instanceof PUCommsAPIImpl impl) {
            return impl.authorisePayment(orderId, amount, cardNumber);
        }
        return commsApi.authorisePayment(orderId, amount);
    }

    // Until IPOS-CA integration code arrives, this records a successful handoff event.
    /**
     * Records a merchant propagation event until full CA integration is available.
     */
    private boolean propagateSaleToMerchant(String orderId,
                                            String userEmail,
                                            String addressLine1,
                                            String addressLine2,
                                            List<OrderLine> lines) {
        String details = "order=" + orderId
                + ", user=" + userEmail
                + ", lineCount=" + lines.size()
                + ", address1=" + addressLine1
                + ", address2=" + (addressLine2 == null ? "" : addressLine2);
        commsApi.recordTransaction(
                "CA-PROP-" + orderId,
                "merchant-propagation",
                "queued",
                LocalDateTime.now().toString()
        );
        return details.length() > 0;
    }

    /**
     * Sends a confirmation and tracking email after a successful checkout.
     */
    private void sendTrackingEmail(String userEmail, String orderId, double finalTotal, boolean discountApplied) {
        String trackingLink = "https://ipos-pu.local/track/" + orderId;
        String body = "Thank you for your purchase.\n\n"
                + "Order ID: " + orderId + "\n"
                + "Total: £" + String.format("%.2f", finalTotal) + "\n"
                + "Discount applied: " + (discountApplied ? "YES (10th order)" : "NO") + "\n"
                + "Tracking link: " + trackingLink + "\n";
        commsApi.sendEmail(userEmail, "Your IPOS-PU order confirmation", body);
    }

    /**
     * Calculates the subtotal value of all order lines before discounts.
     */
    private double calculateSubtotal(List<OrderLine> items) {
        double sum = 0;
        for (OrderLine line : items) {
            sum += line.lineTotal();
        }
        return sum;
    }

    /**
     * Represents a single product line within an order.
     */
    public record OrderLine(String productId,
                            String productName,
                            int quantity,
                            double unitPrice,
                            String campaignId) {
        // checks if order line has usable data
        public boolean isValid() {
            return productId != null && !productId.trim().isEmpty()
                    && productName != null && !productName.trim().isEmpty()
                    && quantity > 0
                    && unitPrice >= 0;
        }
        // calculates total value of order line
        public double lineTotal() {
            return quantity * unitPrice;
        }
    }

    /**
     * Represents the result of a checkout attempt.
     */
    public record CheckoutResult(boolean success,
                                 String orderId,
                                 double finalTotal,
                                 boolean discountApplied,
                                 boolean propagatedToMerchant,
                                 String message) {
        // successful checkout result
        public static CheckoutResult success(String orderId,
                                             double finalTotal,
                                             boolean discountApplied,
                                             boolean propagatedToMerchant) {
            return new CheckoutResult(true, orderId, finalTotal, discountApplied, propagatedToMerchant, "Order completed.");
        }

        /**
         * Creates a failed checkout result with no order reference.
         */
        public static CheckoutResult failure(String message) {
            return new CheckoutResult(false, null, 0, false, false, message);
        }

        /**
         * Creates a failed checkout result where an order record already exists.
         */
        public static CheckoutResult failureWithOrder(String orderId, double finalTotal, String message) {
            return new CheckoutResult(false, orderId, finalTotal, false, false, message);
        }
    }

    /**
     * Represents a summary view of a saved order.
     */
    public record OrderSummary(String orderId,
                               String orderDateTime,
                               int itemCount,
                               String status,
                               double totalAmount) {
    }
}
