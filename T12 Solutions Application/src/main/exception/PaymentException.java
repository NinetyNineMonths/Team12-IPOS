package main.exception;

/**
 * Thrown when payment processing or payment state transitions fail.
 */
public class PaymentException extends Exception {
    /**
     * Creates a payment exception with a message.
     */
    public PaymentException(String message) {
        super(message);
    }

    /**
     * Creates a payment exception with a message and root cause.
     */
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}