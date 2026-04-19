package main.api;

/**
 * Interface for communications and transaction support used by IPOS-PU.
 *
 * This interface defines operations for sending emails,
 * authorising payments, and recording transaction events.
 */
public interface PUCommsAPI {

    /**
     * Sends an email message to a specified recipient.
     */
    boolean sendEmail(String to, String subject, String body);

    /**
     * Authorises a payment for a given order and amount.
     */
    boolean authorisePayment(String orderId, double amount);

    /**
     * Records a transaction or system event for tracking purposes.
     */
    void recordTransaction(String refId, String type, String outcome, String timestamp);
}