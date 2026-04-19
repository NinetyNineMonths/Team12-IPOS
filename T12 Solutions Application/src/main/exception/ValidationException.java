package main.exception;

/**
 * Thrown when input or business-rule validation fails.
 */
public class ValidationException extends Exception {
    /**
     * Creates a validation exception with a message.
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Creates a validation exception with a message and root cause.
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}