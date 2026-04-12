package main.implementation;

import main.api.PUCommsAPI;
import main.api.SAMerchantApplicationAPI;
import main.db.DatabaseManager;
import main.exception.IntegrationException;
import main.exception.NotFoundException;
import main.exception.ValidationException;
import main.model.CommercialApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SAMerchantApplicationAPIImpl implements SAMerchantApplicationAPI {

    private final PUCommsAPI puCommsAPI = new PUCommsAPIImpl();

    @Override
    public void submitMerchantApplication(CommercialApplication application)
            throws ValidationException, IntegrationException {

        if (application == null) {
            throw new ValidationException("Application must not be null.");
        }
        if (application.getApplicationId() == null || application.getApplicationId().trim().isEmpty()) {
            throw new ValidationException("Application ID must not be empty.");
        }

        String sql = """
            INSERT INTO commercial_applications (
                application_id,
                company_name,
                business_type,
                address_line_1,
                address_line_2,
                city,
                postcode,
                company_house_registration,
                director_name,
                director_contact,
                email,
                notification_method,
                status,
                submitted_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, application.getApplicationId());
            ps.setString(2, application.getCompanyName());
            ps.setString(3, application.getBusinessType());
            ps.setString(4, application.getAddressLine1());
            ps.setString(5, application.getAddressLine2());
            ps.setString(6, application.getCity());
            ps.setString(7, application.getPostcode());
            ps.setString(8, application.getCompanyHouseRegistration());
            ps.setString(9, application.getDirectorName());
            ps.setString(10, application.getDirectorContact());
            ps.setString(11, application.getEmail());
            ps.setString(12, application.getNotificationMethod());
            ps.setString(13, application.getStatus());
            ps.setString(14, application.getSubmittedAt().toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IntegrationException("Failed to submit merchant application.", e);
        }
    }

    @Override
    public CommercialApplication getApplicationById(String applicationId)
            throws ValidationException, NotFoundException, IntegrationException {

        if (applicationId == null || applicationId.trim().isEmpty()) {
            throw new ValidationException("Application ID must not be empty.");
        }

        String sql = """
            SELECT *
            FROM commercial_applications
            WHERE application_id = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, applicationId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFoundException("Commercial application not found: " + applicationId);
                }

                return mapCommercialApplication(rs);
            }

        } catch (SQLException e) {
            throw new IntegrationException("Failed to retrieve merchant application.", e);
        }
    }

    @Override
    public List<CommercialApplication> getPendingApplications()
            throws IntegrationException {

        String sql = """
            SELECT *
            FROM commercial_applications
            WHERE status = 'PENDING'
            ORDER BY submitted_at ASC
        """;

        List<CommercialApplication> applications = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                applications.add(mapCommercialApplication(rs));
            }

            return applications;

        } catch (SQLException e) {
            throw new IntegrationException("Failed to retrieve pending merchant applications.", e);
        }
    }

    @Override
    public void updateApplicationStatus(String applicationId, String newStatus)
            throws ValidationException, NotFoundException, IntegrationException {

        if (applicationId == null || applicationId.trim().isEmpty()) {
            throw new ValidationException("Application ID must not be empty.");
        }
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new ValidationException("New status must not be empty.");
        }

        String normalisedStatus = newStatus.trim().toUpperCase();
        if (!normalisedStatus.equals("PENDING")
                && !normalisedStatus.equals("APPROVED")
                && !normalisedStatus.equals("REJECTED")) {
            throw new ValidationException("Status must be PENDING, APPROVED, or REJECTED.");
        }

        CommercialApplication application = getApplicationById(applicationId);

        String sql = """
            UPDATE commercial_applications
            SET status = ?
            WHERE application_id = ?
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, normalisedStatus);
            ps.setString(2, applicationId.trim());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new NotFoundException("Commercial application not found: " + applicationId);
            }

        } catch (SQLException e) {
            throw new IntegrationException("Failed to update merchant application status.", e);
        }

        notifyApplicantIfRequired(application, normalisedStatus);
    }

    private CommercialApplication mapCommercialApplication(ResultSet rs) throws SQLException {
        return new CommercialApplication(
                rs.getString("application_id"),
                rs.getString("company_name"),
                rs.getString("business_type"),
                rs.getString("address_line_1"),
                rs.getString("address_line_2"),
                rs.getString("city"),
                rs.getString("postcode"),
                rs.getString("company_house_registration"),
                rs.getString("director_name"),
                rs.getString("director_contact"),
                rs.getString("email"),
                rs.getString("notification_method"),
                rs.getString("status"),
                LocalDateTime.parse(rs.getString("submitted_at"))
        );
    }

    private void notifyApplicantIfRequired(CommercialApplication app, String newStatus) {
        if (app == null) {
            return;
        }

        if (!"Email".equalsIgnoreCase(app.getNotificationMethod())) {
            return;
        }

        String subject = "Commercial application update";
        String body;

        if ("APPROVED".equalsIgnoreCase(newStatus)) {
            body = "Dear " + app.getDirectorName() + ",\n\n"
                    + "Your commercial application for " + app.getCompanyName()
                    + " has been approved.\n"
                    + "You may now proceed to access the IPOS-SA services.\n\n"
                    + "Regards,\nIPOS-SA";
        } else if ("REJECTED".equalsIgnoreCase(newStatus)) {
            body = "Dear " + app.getDirectorName() + ",\n\n"
                    + "Your commercial application for " + app.getCompanyName()
                    + " has been rejected.\n\n"
                    + "Regards,\nIPOS-SA";
        } else {
            return;
        }

        puCommsAPI.sendEmail(app.getEmail(), subject, body);
    }
}