package main.exception;

/**
 * Thrown when communication with external systems fails.
 */
public class IntegrationException extends Exception {
    /**
     * Creates an integration exception with a message.
     */
    public IntegrationException(String message) {
        super(message);
    }

    /**
     * Creates an integration exception with a message and root cause.
     */
    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}