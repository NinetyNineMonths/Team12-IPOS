package main.ui;

import main.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Simple database viewer used for admin inspection.
 *
 * This class allows the user to choose a database table
 * and display its contents in a Swing table.
 */

public class DatabaseViewer extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> tableSelector;

    /**
     * Constructs the database viewer window and loads
     * the default table on startup.
     */
    public DatabaseViewer() {
        setTitle("Database Viewer");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(new JLabel("Select Table:"));

        tableSelector = new JComboBox<>(new String[]{
                "users",
                "orders",
                "order_items",
                "campaigns",
                "campaign_items",
                "campaign_metrics",
                "campaign_item_metrics",
                "commercial_applications",
                "products",
                "payments",
                "payInfo"
        });

        JButton loadButton = new JButton("Load");

        topPanel.add(tableSelector);
        topPanel.add(loadButton);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadTable((String) tableSelector.getSelectedItem()));

        loadTable("users");
    }

    /**
     * Loads all data from the selected table into the Swing table model.
     */
    private void loadTable(String tableName) {
        model.setRowCount(0);
        model.setColumnCount(0);

        String sql = "SELECT * FROM " + tableName;

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading table '" + tableName + "': " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
