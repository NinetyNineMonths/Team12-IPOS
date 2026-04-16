package main.config;

public class EmailConfig {

    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";

    public static final String FROM_ADDRESS =
            System.getenv().getOrDefault("PU_EMAIL_ADDRESS", "ipospu3@gmail.com");

    public static final String FROM_PASSWORD =
            System.getenv().getOrDefault("PU_EMAIL_PASSWORD", "REPLACE_WITH_APP_PASSWORD");

    public static boolean isConfigured() {
        return !FROM_ADDRESS.equals("REPLACE_WITH_GMAIL_ADDRESS")
                && !FROM_PASSWORD.equals("REPLACE_WITH_APP_PASSWORD")
                && !FROM_ADDRESS.isBlank()
                && !FROM_PASSWORD.isBlank();
    }

    private EmailConfig() {}
}
