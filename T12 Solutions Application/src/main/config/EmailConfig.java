package main.config;

/**
 * Centralised SMTP configuration used for email notifications.
 */
public class EmailConfig {

    // Gmail SMTP endpoint used by the app.
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";

    // Credentials are read from environment variables with local defaults.
    public static final String FROM_ADDRESS =
            System.getenv().getOrDefault("PU_EMAIL_ADDRESS", "ipospu3@gmail.com");

    public static final String FROM_PASSWORD =
            System.getenv().getOrDefault("PU_EMAIL_PASSWORD", "zbvcjxopcfkfrtvb");

    /**
     * Returns true only when non-placeholder sender credentials are available.
     */
    public static boolean isConfigured() {
        return !FROM_ADDRESS.equals("REPLACE_WITH_GMAIL_ADDRESS")
                && !FROM_PASSWORD.equals("REPLACE_WITH_APP_PASSWORD")
                && !FROM_ADDRESS.isBlank()
                && !FROM_PASSWORD.isBlank();
    }

    // Utility class.
    private EmailConfig() {}
}
