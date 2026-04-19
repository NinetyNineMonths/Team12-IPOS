package main.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides JDBC connections to the shared SA PostgreSQL database.
 */
public class SADatabaseManager {

    private static final String URL =
            "jdbc:postgresql://interchange.proxy.rlwy.net:32051/railway";

    private static final String USER =
            System.getenv().getOrDefault("SA_DB_USER", "postgres");

    private static final String PASSWORD =
            System.getenv().getOrDefault("SA_DB_PASSWORD", "lErRygBqPGvHztVNpppUnpSQslZfwRbx");

    // Utility class.
    private SADatabaseManager() {
    }

    /**
     * Opens a new SA database connection using configured credentials.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
