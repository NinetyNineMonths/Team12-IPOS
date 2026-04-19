package main.exception;

/**
 * Thrown when user identity or credentials cannot be verified.
 */
public class AuthenticationException extends Exception {
    /**
     * Creates an authentication exception with a message.
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Creates an authentication exception with a message and root cause.
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}