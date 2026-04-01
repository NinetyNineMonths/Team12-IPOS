package main.db;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:ipos_pu.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initialise() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                email       TEXT    PRIMARY KEY,
                full_name   TEXT    NOT NULL,
                password    TEXT    NOT NULL,
                role        TEXT    NOT NULL DEFAULT 'CUSTOMER',
                first_login INTEGER NOT NULL DEFAULT 1
            );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            seedUsersIfEmpty(conn);
        } catch (SQLException e) {
            throw new RuntimeException("DB init failed", e);
        }
    }

    private static void seedUsersIfEmpty(Connection conn) throws SQLException {
        try (ResultSet rs = conn.createStatement()
                .executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.getInt(1) == 0) {
                conn.createStatement().execute("""
                    INSERT INTO users VALUES
                    ('customer@ipos.com', 'Test Customer', 'Test123!', 'CUSTOMER', 1),
                    ('admin@ipos.com',    'System Admin',  'Admin123!', 'ADMIN',   0)
                """);
            }
        }
    }
}