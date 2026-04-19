package main.implementation;

import main.api.PUCommsAPI;
import main.config.EmailConfig;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.time.LocalDateTime;
import java.util.Properties;


public class PUCommsAPIImpl implements PUCommsAPI {
    /**
     * Sends real emails via Gmail SMTP using Jakarta Mail
     * when credentials are configured.
     * Falls back to console logging if SMTP is not configured or fails,
     *
     * Email flows handled:
     *   - Non-commercial member registration — delivers temporary password
     *   - Order confirmation - itemised receipt with tracking link after checkout
     *   - SA application outcome - approval/rejection notification on behalf of IPOS-SA
     *
     * Payment flows handled:
     *   - Card authorisation with number masking and validation
     *   - Failed payment detection and shows message
     */
    public PUCommsAPIImpl() {
    }
    /**
     * Sends an email via Gmail SMTP if credentials are configured in EmailConfig.
     *  @param to recipient email address; must not be null or blank
     *  @param subject the email subject line
     *  @param body    the plain-text email body; must not be null or blank
     *  @return {@code true} if the email was sent or safely logged as a fallback
     */

    @Override
    public boolean sendEmail(String to, String subject, String body) {
        if (to == null || to.trim().isEmpty()) {
            System.out.println("[EMAIL] Failed: recipient address is missing.");
            return false;
        }
        if (body == null || body.trim().isEmpty()) {
            System.out.println("[EMAIL] Failed: email body is missing.");
            return false;
        }

        if (!EmailConfig.isConfigured()) {
            logToConsole(to, subject, body, "SMTP not configured — logged locally");
            return true;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host",            EmailConfig.SMTP_HOST);
            props.put("mail.smtp.port",            EmailConfig.SMTP_PORT);
            props.put("mail.smtp.auth",            "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            EmailConfig.FROM_ADDRESS,
                            EmailConfig.FROM_PASSWORD
                    );
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.FROM_ADDRESS));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.trim()));
            message.setSubject(subject != null ? subject : "(no subject)");
            message.setText(body);

            Transport.send(message);

            System.out.println("[EMAIL] Sent → " + to + " | Subject: " + subject);
            recordTransaction("EMAIL_" + System.currentTimeMillis(), "email", "sent",
                    LocalDateTime.now().toString());
            return true;

        } catch (MessagingException e) {
            logToConsole(to, subject, body, "SMTP error: " + e.getMessage());
            return true; // fallback — email logged, app continues normally
        }
    }

    /**
     * Validates and records a card payment for an order.
     * @param orderId    the unique order ID
     * @param amount     charge amount in GBP — must be greater than 0
     * @param cardNumber customer card number — must be at least 12 digits
     */

    public boolean authorisePayment(String orderId, double amount, String cardNumber) {
        if (orderId == null || orderId.trim().isEmpty()) {
            System.out.println("[PAYMENT] Declined: orderId is missing.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("[PAYMENT] Declined: amount must be greater than 0.");
            return false;
        }
        if (cardNumber == null || cardNumber.replaceAll("\\s", "").length() < 12) {
            System.out.println("[PAYMENT] Declined: card number must be at least 12 digits.");
            return false;
        }

        String digits = cardNumber.replaceAll("\\s", "");
        String masked = "**** **** **** " + digits.substring(digits.length() - 4);

        System.out.println("[PAYMENT] Authorised: Order " + orderId
                + " | Amount: £" + String.format("%.2f", amount)
                + " | Card: " + masked);

        recordTransaction("PAY_" + orderId, "payment", "authorised",
                LocalDateTime.now().toString());
        return true;
    }

    @Override
    /**
     * Validates and authorises a card payment for an order.
     * The card number is masked to show only the last four digits before logging.
     */
    public boolean authorisePayment(String orderId, double amount) {
        return authorisePayment(orderId, amount, "000000000000");
    }

    @Override
    /**
     * Logs a transaction event to the console
     */
    public void recordTransaction(String refId, String type, String outcome, String timestamp) {
        System.out.println("[TRANSACTION] Ref: " + refId
                + " | Type: " + type
                + " | Outcome: " + outcome
                + " | Time: " + timestamp);
    }


    private void logToConsole(String to, String subject, String body, String reason) {
        System.out.println("[EMAIL] " + reason);
        System.out.println("[EMAIL] To:      " + to);
        System.out.println("[EMAIL] Subject: " + subject);
        System.out.println("[EMAIL] Body:    " + body);
        recordTransaction("EMAIL_" + System.currentTimeMillis(), "email", reason,
                LocalDateTime.now().toString());
    }
}
