package main.exception;

/**
 * Thrown when a requested entity cannot be found.
 */
public class NotFoundException extends Exception {
    /**
     * Creates a not-found exception with a message.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a not-found exception with a message and root cause.
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}