package main.service;

import main.db.DatabaseManager;
import main.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for retrieving and updating product catalogue data.
 *
 * This class loads products from the local database and
 * supports stock updates when orders are placed.
 */

public class CatalogueService {

    /**
     * Retrieves all products from the local product catalogue.
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        String sql = """
            SELECT product_id, product_name, description, package_type, unit_type,
                   pack_size, wholesale_cost, retail_price, stock, stock_limit
            FROM products
            ORDER BY product_id
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("description"),
                        rs.getString("package_type"),
                        rs.getString("unit_type"),
                        rs.getInt("pack_size"),
                        rs.getDouble("wholesale_cost"),
                        rs.getDouble("retail_price"),
                        rs.getInt("stock"),
                        rs.getInt("stock_limit")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Reduces product stock by a given quantity if enough stock is available.
     * Returns true if the stock update succeeds.
     */
    public boolean reduceStock(String productId, int quantity) {
        String sql = """
            UPDATE products
            SET stock = stock - ?
            WHERE product_id = ?
              AND stock >= ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, productId);
            ps.setInt(3, quantity);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}