package main.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SETUP BEFORE RUNNING:
 * Replace REPLACE_WITH_SA_USERNAME and REPLACE_WITH_SA_PASSWORD with the real
 * credentials provided by the SA team — do this on your local machine only.
 *
 * Don't commit the real credentials to GitHub.
 */
public class SADatabaseManager {

    private static final String URL =
            "jdbc:postgresql://interchange.proxy.rlwy.net:32051/railway";

    private static final String USER =
            System.getenv().getOrDefault("SA_DB_USER", "REPLACE_WITH_SA_USERNAME");

    private static final String PASSWORD =
            System.getenv().getOrDefault("SA_DB_PASSWORD", "REPLACE_WITH_SA_PASSWORD");

    private SADatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
