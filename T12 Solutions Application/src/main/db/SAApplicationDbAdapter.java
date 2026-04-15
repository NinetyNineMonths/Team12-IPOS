package main.db;

import main.model.CommercialApplication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate; 

public class SAApplicationDbAdapter {

    public enum SubmitResult {
        SUCCESS,        
        ALREADY_EXISTS, 
        INVALID_INPUT,  
        DB_UNAVAILABLE  
    }

    public SubmitResult submitApplication(CommercialApplication application) {
        if (application == null) {
            System.out.println("[SA-DB] Cannot submit: application is null.");
            return SubmitResult.INVALID_INPUT;
        }

        if (application.getApplicationId() == null || application.getApplicationId().trim().isEmpty()) {
            System.out.println("[SA-DB] Cannot submit: applicationId is missing.");
            return SubmitResult.INVALID_INPUT;
        }

        if (application.getEmail() == null || application.getEmail().trim().isEmpty()) {
            System.out.println("[SA-DB] Cannot submit: email is missing.");
            return SubmitResult.INVALID_INPUT;
        }

        String sql = """
            INSERT INTO pu_applications (
                application_id,
                type,
                email,
                submitted_at,
                status,
                company_name,
                company_house_reg,
                director_name,
                business_type,
                address,
                notes
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (application_id) DO NOTHING
        """;

        try (Connection conn = SADatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, application.getApplicationId().trim());
            ps.setString(2, "commercial");
            ps.setString(3, application.getEmail().trim());
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.setString(5, "PENDING");
            ps.setString(6, application.getCompanyName());
            ps.setString(7, application.getCompanyHouseRegistration());
            ps.setString(8, application.getDirectorName());
            ps.setString(9, application.getBusinessType());
            ps.setString(10, buildAddress(application));
            ps.setString(11, buildNotes(application));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("[SA-DB] Application submitted to SA: " + application.getApplicationId());
                return SubmitResult.SUCCESS;
            } else {
                System.out.println("[SA-DB] Application already exists in SA: " + application.getApplicationId());
                return SubmitResult.ALREADY_EXISTS;
            }

        } catch (SQLException e) {
            System.out.println("[SA-DB] Could not connect to SA database: " + e.getMessage());
            return SubmitResult.DB_UNAVAILABLE;
        }
    }

    public String getApplicationStatus(String applicationId) {
        if (applicationId == null || applicationId.trim().isEmpty()) {
            return "NOT_FOUND";
        }

        String sql = "SELECT status FROM pu_applications WHERE application_id = ?";

        try (Connection conn = SADatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, applicationId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
                return "NOT_FOUND";
            }

        } catch (SQLException e) {
            System.out.println("[SA-DB] Could not read status from SA: " + e.getMessage());
            return "DB_UNAVAILABLE";
        }
    }

    public boolean isReachable() {
        try (Connection conn = SADatabaseManager.getConnection()) {
            return conn != null && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private String buildAddress(CommercialApplication application) {
        StringBuilder sb = new StringBuilder();
        appendIfNotBlank(sb, application.getAddressLine1());
        appendIfNotBlank(sb, application.getAddressLine2());
        appendIfNotBlank(sb, application.getCity());
        appendIfNotBlank(sb, application.getPostcode());
        String result = sb.toString();
        return result.length() > 500 ? result.substring(0, 500) : result;
    }

    private String buildNotes(CommercialApplication application) {
        return "directorContact=" + safe(application.getDirectorContact())
                + "; notificationMethod=" + safe(application.getNotificationMethod());
    }

    private void appendIfNotBlank(StringBuilder sb, String value) {
        if (value == null || value.trim().isEmpty()) return;
        if (sb.length() > 0) sb.append(", ");
        sb.append(value.trim());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
