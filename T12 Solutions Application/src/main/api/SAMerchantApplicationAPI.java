package main.api;

/**
 * Interface for commercial membership application communication
 * with the SA subsystem.
 *
 * This interface supports submitting merchant applications
 * and retrieving their current review status.
 */

public interface SAMerchantApplicationAPI {

    /**
     * Submits a merchant application to the SA subsystem.
     */
    String submitMerchantApplication(String application);

    /**
     * Returns the current status of a submitted application.
     */
    String getApplicationStatus(String applicationId);
}