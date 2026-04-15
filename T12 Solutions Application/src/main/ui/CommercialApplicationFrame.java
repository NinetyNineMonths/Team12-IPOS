package main.ui;

import main.db.SAApplicationDbAdapter;
import main.model.CommercialApplication;
import main.service.AuthService;
import main.service.CommercialApplicationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class CommercialApplicationFrame extends JFrame {

    private final AuthService authService;
    private final CommercialApplicationService applicationService;

    private JTextField companyNameField;
    private JTextField businessTypeField;
    private JTextField addressLine1Field;
    private JTextField addressLine2Field;
    private JTextField cityField;
    private JTextField postcodeField;
    private JTextField companyHouseField;
    private JTextField directorNameField;
    private JTextField directorContactField;
    private JTextField emailField;
    private JComboBox<String> notificationMethodBox;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public CommercialApplicationFrame(AuthService authService) {
        this.authService = authService;
        this.applicationService = new CommercialApplicationService();

        setTitle("Commercial Membership Application");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(root);

        JLabel title = new JLabel("Apply for Commercial Membership", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(12, 2, 10, 10));

        form.add(new JLabel("Company Name:"));
        companyNameField = new JTextField();
        form.add(companyNameField);

        form.add(new JLabel("Business Type:"));
        businessTypeField = new JTextField();
        form.add(businessTypeField);

        form.add(new JLabel("Address Line 1:"));
        addressLine1Field = new JTextField();
        form.add(addressLine1Field);

        form.add(new JLabel("Address Line 2 (optional):"));
        addressLine2Field = new JTextField();
        form.add(addressLine2Field);

        form.add(new JLabel("City / Town:"));
        cityField = new JTextField();
        form.add(cityField);

        form.add(new JLabel("Postcode:"));
        postcodeField = new JTextField();
        form.add(postcodeField);

        form.add(new JLabel("Company House Registration:"));
        companyHouseField = new JTextField();
        form.add(companyHouseField);

        form.add(new JLabel("Director Name:"));
        directorNameField = new JTextField();
        form.add(directorNameField);

        form.add(new JLabel("Director Contact:"));
        directorContactField = new JTextField();
        form.add(directorContactField);

        form.add(new JLabel("Email Address:"));
        emailField = new JTextField();
        form.add(emailField);

        form.add(new JLabel("Notification Method:"));
        notificationMethodBox = new JComboBox<>(new String[]{"Email", "Post"});
        form.add(notificationMethodBox);

        JButton submitButton = new JButton("Submit Application");
        JButton backButton = new JButton("Back");

        form.add(submitButton);
        form.add(backButton);

        root.add(form, BorderLayout.CENTER);

        submitButton.addActionListener(e -> handleSubmit());

        backButton.addActionListener(e -> {
            new WelcomeFrame(authService).setVisible(true);
            dispose();
        });
    }

    private void handleSubmit() {
        String companyName              = companyNameField.getText().trim();
        String businessType             = businessTypeField.getText().trim();
        String addressLine1             = addressLine1Field.getText().trim();
        String addressLine2             = addressLine2Field.getText().trim();
        String city                     = cityField.getText().trim();
        String postcode                 = postcodeField.getText().trim();
        String companyHouseRegistration = companyHouseField.getText().trim();
        String directorName             = directorNameField.getText().trim();
        String directorContact          = directorContactField.getText().trim();
        String email                    = emailField.getText().trim().toLowerCase();
        String notificationMethod       = (String) notificationMethodBox.getSelectedItem();

        // Validate required fields
        if (companyName.isEmpty() || businessType.isEmpty() || addressLine1.isEmpty()
                || city.isEmpty() || postcode.isEmpty() || companyHouseRegistration.isEmpty()
                || directorName.isEmpty() || directorContact.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String applicationId = "PU-COM-" + System.currentTimeMillis();

        CommercialApplication application = new CommercialApplication(
                applicationId,
                companyName,
                businessType,
                addressLine1,
                addressLine2,
                city,
                postcode,
                companyHouseRegistration,
                directorName,
                directorContact,
                email,
                notificationMethod,
                "PENDING",
                LocalDateTime.now()
        );

        try {
            // Check for duplicates in PU's local database first
            if (applicationService.emailExistsForApplication(email)) {
                JOptionPane.showMessageDialog(
                        this,
                        "An application with this email already exists.",
                        "Duplicate Application",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (applicationService.companyHouseExists(companyHouseRegistration)) {
                JOptionPane.showMessageDialog(
                        this,
                        "An application with this Company House registration already exists.",
                        "Duplicate Application",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Step 1 — save locally to PU's own SQLite database
            applicationService.saveApplication(application);

            // Step 2 — forward to SA's shared PostgreSQL database.
            // SA will review, update status, and send the outcome email via their own system.
            // PU does NOT send the approval/rejection email — SA handles that.
            SAApplicationDbAdapter saAdapter = new SAApplicationDbAdapter();
            SAApplicationDbAdapter.SubmitResult saResult = saAdapter.submitApplication(application);

            String successMessage = "Commercial application submitted successfully.\n\n"
                    + "Application ID: " + applicationId + "\n"
                    + "Status: PENDING\n";

            switch (saResult) {
                case SUCCESS ->
                    successMessage += "\nForwarded to InfoPharma (SA) for review.\n"
                            + "You will be notified of the outcome by " + notificationMethod + ".";
                case ALREADY_EXISTS ->
                    successMessage += "\nNote: Application already on record with SA.";
                case INVALID_INPUT ->
                    successMessage += "\nNote: Application data was invalid — saved locally only.";
                default ->
                    // DB_UNAVAILABLE — SA unreachable, but PU saved locally so no data is lost
                    successMessage += "\nNote: Could not reach SA system right now.\n"
                            + "Your application has been saved locally.";
            }

            JOptionPane.showMessageDialog(
                    this,
                    successMessage,
                    "Application Submitted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            new WelcomeFrame(authService).setVisible(true);
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save application:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
